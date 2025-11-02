package org.kitona.zus.business.entity.bo;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权模型图
 *
 * @param graph 有向图
 */
@Slf4j
public record AuthorizationModelGraph(DirectedGraph graph, AuthorizationModel model) {

    public static AuthorizationModelGraph fromModel(AuthorizationModel model) {
        AuthorizationModelGraphBuilder builder = new AuthorizationModelGraphBuilder();

        Map<String, TypeDefinition> resTypeMapDef = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));
        for (var resourceType : resTypeMapDef.keySet()) {
            // 1. 添加资源类型节点，例如 "document"
            builder.getOrAddNode(resourceType, resourceType, NodeType.SPECIFIC_TYPE);

            TypeDefinition typeDef = resTypeMapDef.get(resourceType);
            // 按RelationName进行排序，即，viewer,owner,editor
            List<String> relations = new ArrayList<>(typeDef.relations().keySet());
            Collections.sort(relations);

            for (var relationName : relations) {
                // 每个relation都生成一个节点，例如 "document#viewer"
                String uniqueLabel = resourceType + "#" + relationName;
                GraphNode parentNode = builder.getOrAddNode(uniqueLabel, uniqueLabel, NodeType.SPECIFIC_TYPE_AND_RELATION);

                RelationDefinition relDef = typeDef.relations().get(relationName);

                // 模拟 checkRewrite — 解析 rewriteExpression 里的依赖关系
                parseRewriteExpression(builder, resTypeMapDef, parentNode, relDef, typeDef.resourceType());
            }
        }

        return new AuthorizationModelGraph(builder.build(), model);
    }

    private static void parseRewriteExpression(AuthorizationModelGraphBuilder builder,
                                               Map<String, TypeDefinition> resTypeMapDef, GraphNode parent,
                                               RelationDefinition relDef, String currentResourceType) {
        // 示例：rewriteExpression = "viewer or editor" => 生成两个边
        if (relDef.rewriteExpression() == null) {
            return;
        }

        String expr = relDef.rewriteExpression().trim();

        // ✅ 特殊处理 1.tupleToUserset (TTU) - 规范图模型中，它只指向自身，表示一个数据源
        if (expr.startsWith("tupleToUserset:")) {
            // 规范：TTU 关系（如 document#parentFolder）是一个元组集，只指向自身
            GraphNode ttuNode = builder.getOrAddNode(parent.id(), parent.id(), NodeType.TUPLE_TO_USER_SET);
            builder.addEdge(parent, ttuNode);
            return;
        }


        String[] refs = expr.split("\\s+or\\s+");
        for (String ref : refs) {
            ref = ref.trim();
            if (ref.isEmpty()) {
                continue;
            }
            String fullRelRef = currentResourceType + "#" + ref;

            // ✅ 特殊处理 2.self 关系（即直接元组）指向自身节点
            if (ref.equals("self")) {
                GraphNode tupleNode = builder.getOrAddNode(parent.id(), parent.id(), NodeType.TUPLE_TO_USER_SET);
                builder.addEdge(parent, tupleNode);
                continue;
            }

            // ✅ 处理外部引用 (type#relation)：例如 document#parentFolder#owner (在FGA中简化为 parentFolder#owner)
            if (ref.contains("#")) {
                String targetId = parseRefRewriteExpression(resTypeMapDef, ref, currentResourceType);
                GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
                builder.addEdge(parent, targetNode);
                continue;
            }

            // ✅ 普通 rewrite 表达式 相对引用（如 owner、editor）
            GraphNode child = builder.getOrAddNode(fullRelRef, fullRelRef, NodeType.SPECIFIC_TYPE_AND_RELATION);
            builder.addEdge(parent, child);
        }
    }

    /**
     * 处理外部引用
     * 处理结果为 "document#parentFolder" -> "folder#owner"
     *
     * @param resTypeMapDef       资源类型和关系映射
     * @param ref                 关系引用，例如 "parentFolder#owner"
     * @param currentResourceType 当前资源类型
     * @return 对应的关系引用 "folder#owner"
     */

    private static String parseRefRewriteExpression(Map<String, TypeDefinition> resTypeMapDef, String ref,
                                                    String currentResourceType) {
        String[] parts = ref.split("#");
        String tupleKeyRelation = parts[0]; // 例如: "parentFolder"
        String targetRelation = parts[1];   // 例如: "owner"

        // 1. 查找当前 resourceType 的 TypeDefinition
        TypeDefinition currentTypeDefinition = resTypeMapDef.get(currentResourceType);

        if (currentTypeDefinition == null) {
            log.warn("AuthorizationModelGraph parseRefRewriteExpression parse TypeDefinition not found for resourceType:{}", currentResourceType);
            return null;
        }

        RelationDefinition relationDefinition = currentTypeDefinition.relations().get(tupleKeyRelation);
        String expr = relationDefinition.rewriteExpression();
        if (expr.startsWith("tupleToUserset:") && expr.contains("to=")) {
            // 提取 'to=' 后面的目标关系，即依赖的终点
            int toIndex = expr.indexOf("to=");
            String targetType = expr.substring(toIndex + 3).trim().split("#")[0];
            return targetType + "#" + targetRelation;
        }
        // 构造完整的依赖目标 ID: folder#owner
        return null;
    }
}
