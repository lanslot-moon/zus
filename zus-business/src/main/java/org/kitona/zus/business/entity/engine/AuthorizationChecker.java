package org.kitona.zus.business.entity.engine;

import org.kitona.zus.business.entity.bo.RelationDefinition;
import org.kitona.zus.business.entity.bo.RelationTuple;
import org.kitona.zus.business.entity.bo.TupleStore;
import org.kitona.zus.business.entity.bo.TypeDefinition;
import org.kitona.zus.business.entity.graph.AuthorizationModelGraph;
import org.kitona.zus.business.entity.graph.GraphNode;
import org.kitona.zus.business.entity.graph.NodeType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限检查器（AuthorizationChecker），遵循 OpenFGA 的递归检查标准。
 * * 关键修正：TTU 链接查找 (findTtuLinkRelation) 现在使用 Type Restrictions。
 */
public class AuthorizationChecker {

    private final AuthorizationModelGraph graph;
    private final TupleStore tupleStore;
    // 存储 TypeDefinition，用于快速查找类型定义和 Type Restrictions
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
        String[] subjectObject = user.split(":");
        String[] resourceObject = resource.split(":");
        String subjectType = subjectObject[0];
        String subjectId = subjectObject[1];

        String resourceType = resourceObject[0];
        String resourceId = resourceObject[1];

        return resolve(subjectType, subjectId, resourceType, resourceId, relation, visited);
    }

    /**
     * 递归核心方法：尝试证明 (user, resource:resourceId, relation) 元组的存在性
     *
     * @param subjectType  用户/对象ID (e.g., "user:alice", "group:devs")
     * @param subjectId    用户/对象ID (e.g., "alice", "devs")
     * @param resourceId   当前检查的资源实例ID (e.g., "document:99")
     * @param resourceType 当前检查的资源类型 (e.g., "document")
     * @param relationName     当前检查的权限关系 (e.g., "writer")
     * @param visited      访问历史 Set，用于防止循环依赖
     * @return 是否有权限
     */
    private boolean resolve(String subjectType, String subjectId, String resourceType, String resourceId, String relationName, Set<String> visited) {
        String currentKey = String.format("%s:%s@%s:%s#%s", subjectType, subjectId, resourceType, resourceId, relationName);
        if (visited.contains(currentKey)) {
            return false; // 检测到循环依赖
        }
        visited.add(currentKey);

        String startNodeId = resourceType + "#" + relationName;
        GraphNode startNode = graph.graph().getGraphNode(startNodeId);
        if (startNode == null) {
            return false; // 模型中没有定义此关系
        }

        // 1. 检查 'self' (元组) 依赖
        // 查找直接的元组 (user, resourceId, relation) 或 (userType:*, resourceId, relation)
        if (tupleStore.hasTuple(subjectType, subjectId, resourceType, resourceId, relationName)) {
            visited.remove(currentKey);
            return true;
        }

        // 2. 检查 'computedUserset' 和 'tupleToUserset' 依赖 (图边)
        for (String neighborId : graph.graph().getNeighbors(startNode)) {
            GraphNode neighbor = graph.graph().getGraphNode(neighborId);

            // 检查图节点类型，忽略 SPECIFIC_TYPE 等节点
            if (neighbor.type() != NodeType.SPECIFIC_TYPE_AND_RELATION) {
                continue;
            }

            // 2.1. Computed Userset (e.g., document#writer -> document#editor)
            // 目标类型与当前资源类型相同
            if (neighborId.startsWith(resourceType + "#")) {
                // 递归检查当前资源上的目标关系
                String targetRelation = neighborId.split("#")[1];
                if (resolve(subjectType, subjectId, resourceType, resourceId, targetRelation, visited)) {
                    visited.remove(currentKey);
                    return true;
                }
                continue;
            }

            // 2.2. Tuple To Userset (TTU) (e.g., document#writer -> folder#editor)

            // 目标类型与当前资源类型不同 (TTU 依赖)

            // 🌟 OpenFGA 标准修正：查找 TTU 链接关系（例如 "parentFolder"）
            String ttuLinkRel = findTtuLinkRelation(neighborId, resourceType);

            // 修正后的 TTU 递归检查逻辑
            if (ttuLinkRel != null) {
                // 目标关系名 (e.g., "editor")
                String targetRelationName = neighborId.split("#")[1];

                // 1. 查找所有以当前资源为起点，以 ttuLinkRel 为关系名的元组
                //    查询 (document, 99, parentFolder) -> 结果是 (document:99, parentFolder, folder:1)
                // ⚠️ 假设您的 findTuples 方法签名是 findTuples(resourceType, resourceId, relation)
                Set<RelationTuple> linkTuples = tupleStore.findTuples(resourceType, resourceId, ttuLinkRel);

                // 2. 对每个父对象进行递归检查
                for (RelationTuple linkTuple : linkTuples) {

                    // **🌟 核心修正：父资源信息在 user 字段中！🌟**
                    String parentResourceType = linkTuple.subjectType(); // e.g., "folder"
                    String parentResourceId = linkTuple.subjectId();   // e.g., "1"

                    // 递归检查：user 对 parentResource 是否拥有 targetRelation 权限
                    // 例如：检查 alice 对 folder:1 是否拥有 'editor' 权限
                    if (resolve(subjectType, subjectId, parentResourceType, parentResourceId, targetRelationName, visited)) {
                        // 找到路径，返回成功
                        visited.remove(currentKey);
                        return true;
                    }
                }
            }
        }

        visited.remove(currentKey);
        return false;
    }


    /**
     * 【OpenFGA 标准 TTU 链接查找】
     * * 查找给定目标关系（targetNodeId，如 folder#editor）的 TTU 元组键关系（如 parentFolder）。
     * 这通过查询 Type Restrictions 实现。
     * * @param targetNodeId 目标权限节点 ID (e.g., "folder#editor")
     *
     * @param currentResourceType 当前资源类型 (e.g., "document")
     * @return TTU 链接关系名 (e.g., "parentFolder")，如果找不到则返回 null。
     */
    private String findTtuLinkRelation(String targetNodeId, String currentResourceType) {
        String[] parts = targetNodeId.split("#");
        if (parts.length != 2) {
            return null; // 目标 ID 格式错误
        }

        String targetType = parts[0]; // e.g., "folder"

        TypeDefinition currentTypeDefinition = typeMap.get(currentResourceType);
        if (currentTypeDefinition == null) {
            return null;
        }

        // 遍历当前资源类型的所有关系，查找 Type Restrictions 匹配的元组键关系
        for (var entry : currentTypeDefinition.relations().entrySet()) {
            String relationName = entry.getKey(); // e.g., "parentFolder"
            RelationDefinition relationDef = entry.getValue();

            // 1. 检查它是否是元组键关系 (OpenFGA 标准：表达式为 'self')
            if ("self".equals(relationDef.rewriteExpression())) {

                // 2. 检查 Type Restrictions 是否包含目标类型
                Set<String> restrictions = currentTypeDefinition.getRestrictionsForRelation(relationName);

                for (String restriction : restrictions) {
                    // 限制可以是 "folder" 或 "folder#owner"。我们只需要匹配类型部分。
                    if (restriction.equals(targetType) || restriction.startsWith(targetType + "#")) {
                        return relationName; // 返回链接关系名 "parentFolder"
                    }
                }
            }
        }
        return null; // 未找到匹配的链接关系
    }
}