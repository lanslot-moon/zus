package org.kitona.zus.business.entity.bo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限检查器，遵循 OpenFGA 的递归检查标准。
 */
public class AuthorizationChecker {

    private final AuthorizationModelGraph graph;
    private final TupleStore tupleStore;
    private final Map<String, TypeDefinition> typeMap;

    public AuthorizationChecker(AuthorizationModelGraph graph, List<RelationTuple> tuples) {
        this.graph = graph;
        this.tupleStore = new TupleStore(tuples);
        this.typeMap = graph.model().typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));
    }

    /**
     * 公共入口方法：检查用户是否对资源拥有某个权限
     *
     * @param user     用户ID (e.g., "user:alice")
     * @param resource 资源实例ID (e.g., "document:99")
     * @param relation 权限关系 (e.g., "writer")
     * @return 是否有权限
     */
    public boolean check(String user, String resource, String relation) {
        // 使用一个 Set 来追踪访问过的状态，防止递归循环
        Set<String> visited = new HashSet<>();
        String resourceType = resource.split(":")[0];

        return resolve(user, resource, resourceType, relation, visited);
    }

    /**
     * 递归核心方法
     *
     * @param user         用户/对象ID
     * @param resourceId   当前检查的资源实例ID (e.g., "document:99")
     * @param resourceType 当前检查的资源类型 (e.g., "document")
     * @param relation     权限关系 (e.g., "writer")
     * @param visited      递归访问记录 (防止循环)
     * @return 是否有权限
     */
    private boolean resolve(String user, String resourceId, String resourceType, String relation, Set<String> visited) {
        String state = String.format("%s#%s#%s", user, resourceId, relation);
        if (visited.contains(state)) {
            return false; // 避免循环依赖
        }
        visited.add(state);

        String currentNodeId = resourceType + "#" + relation;
        Set<String> dependencies = graph.graph().getEdges().getOrDefault(currentNodeId, Collections.emptySet());

        // 1. 遍历图依赖 (Rewrite Logic)
        for (String targetNodeId : dependencies) {

            String[] targetParts = targetNodeId.split("#");
            String targetType = targetParts[0];
            String targetRelation = targetParts[1];

            // 1.1. 统一处理自循环 (Self/TupleSet 关系: currentNodeId == targetNodeId)
            if (currentNodeId.equals(targetNodeId)) {

                // 检查当前关系 (relation) 的定义是 self 还是 TTU。
                RelationDefinition relDef = typeMap.get(resourceType).relations().get(relation);
                String expr = relDef.rewriteExpression();
                if (expr.startsWith("tupleToUserset:")) {
                    // 情况 1.1.1: 如果是 TTU 关系 (例如 document#parentFolder)
                    // 此时，user 必须是 resourceId 的 parentFolder。
                    // TTU 关系本质上是元组集，因此只需检查直连元组
                    if (tupleStore.checkDirectTuple(user, resourceId, relation)) {
                        return true;
                    }
                } else if (expr.contains("self")) {
                    // 情况 1.1.2: 如果包含 self (例如 document#writer 的 self 部分)
                    // 检查用户是否通过直连元组拥有权限
                    if (tupleStore.checkDirectTuple(user, resourceId, relation)) {
                        return true;
                    }
                }

                continue; // 自循环检查完毕，继续下一条边
            }

            // 1.2. 内部依赖 (Internal Rewrite, e.g., folder#editor -> folder#owner)
            if (targetType.equals(resourceType)) {
                if (resolve(user, resourceId, targetType, targetRelation, visited)) {
                    return true;
                }
            }

            // 1.3. 跨对象依赖 (Computed Userset / TTU Jump, e.g., document#writer -> folder#editor)
            else {
                // 发现跨类型跳转，需要查找 TTU 规则来找到中间对象

                // 确定链接关系 (Linking Relation)
                // 必须从原始模型中查找哪个关系定义指向了 targetNodeId (e.g., folder#editor)
                String linkingRelation = findLinkingRelation(resourceType, relation, targetNodeId);

                if (linkingRelation == null) {
                    // 理论上不应该发生，除非模型编译有误
                    continue;
                }

                // 查找中间对象 (Intermediate Resources)
                // 这一步利用了 TTU 规则的 from=document#parentFolder 部分
                List<String> linkedResources = tupleStore.findLinkedResources(resourceId, linkingRelation);

                for (String linkedResourceId : linkedResources) {
                    String linkedResourceType = linkedResourceId.split(":")[0];

                    // 递归调用：检查用户对中间对象是否拥有目标权限
                    if (resolve(user, linkedResourceId, linkedResourceType, targetRelation, visited)) {
                        return true;
                    }
                }
            }
        }

        visited.remove(state); // 回溯
        return false;
    }

    /**
     * 辅助函数：根据编译后的图边，反推原始模型中的链接关系 (e.g., "parentFolder")
     *
     * @param currentType     当前类型 (document)
     * @param currentRelation 当前关系 (writer)
     * @param targetNodeId    目标节点ID (folder#editor)
     * @return 链接关系名称 (parentFolder)
     */
    private String findLinkingRelation(String currentType, String currentRelation, String targetNodeId) {
        TypeDefinition typeDef = typeMap.get(currentType);
        if (typeDef == null) return null;

        RelationDefinition relDef = typeDef.relations().get(currentRelation);
        if (relDef == null || relDef.rewriteExpression() == null) return null;

        String expr = relDef.rewriteExpression();
        String[] refs = expr.split("\\s+or\\s+");

        for (String ref : refs) {
            if (ref.contains("#")) { // 例如：parentFolder#editor
                String[] parts = ref.split("#");
                String alias = parts[0]; // parentFolder
                String aliasTargetRel = parts[1]; // editor

                // 编译时我们知道：parentFolder 关系被编译到了 folder 类型
                // 检查编译结果是否匹配：alias#aliasTargetRel 编译后是否等于 targetNodeId
                try {
                    String compiledTarget = parseRefRewriteExpression(typeMap, ref, currentType);
                    if (targetNodeId.equals(compiledTarget)) {
                        return alias; // 返回链接关系名称 "parentFolder"
                    }
                } catch (Exception e) {
                    // 忽略解析错误
                }
            }
        }
        return null; // 未找到匹配的链接关系
    }

    // ⚠️ 确保您的 AuthorizationModelGraph.java 中 parseRefRewriteExpression 逻辑是正确的，
    // 这里复用其功能来反推编译结果。
    private String parseRefRewriteExpression(Map<String, TypeDefinition> resTypeMapDef, String ref, String currentResourceType) {
        // ... (此处省略您提供的 parseRefRewriteExpression 完整代码，因为它就是查找 TTU 并返回编译后的目标 ID)
        // 此处应将您提供的 parseRefRewriteExpression 逻辑粘贴进来
        String[] parts = ref.split("#");
        String tupleKeyRelation = parts[0];
        String targetRelation = parts[1];
        TypeDefinition currentTypeDefinition = resTypeMapDef.get(currentResourceType);
        RelationDefinition relationDefinition = currentTypeDefinition.relations().get(tupleKeyRelation);
        String expr = relationDefinition.rewriteExpression();
        if (expr.startsWith("tupleToUserset:") && expr.contains("to=")) {
            int toIndex = expr.indexOf("to=");
            String targetType = expr.substring(toIndex + 3).trim().split("#")[0];
            return targetType + "#" + targetRelation;
        }
        return null;
    }
}