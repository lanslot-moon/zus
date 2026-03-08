package org.kitona.zus.domain.service.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 有向图
 * <p>
 * 用于表示授权模型中类型和关系的依赖结构。
 * 节点 key 为 document#viewer 等形式（resourceType#relation），
 * 边表示 resourceType#relation 之间的依赖关系。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public class DirectedGraph {

    private final Map<String, GraphNode> nodes = new HashMap<>();
    private final Map<String, Set<String>> edges = new HashMap<>();

    /**
     * 向图中添加节点；若已存在则返回已有节点
     *
     * @param id    节点唯一标识
     * @param label 显示标签
     * @param type  节点类型
     * @return 节点
     */
    public GraphNode addNode(String id, String label, NodeType type) {
        return nodes.computeIfAbsent(id, k -> new GraphNode(id, label, type));
    }

    /**
     * 添加从 fromId 到 toId 的有向边
     */
    public void addEdge(String fromId, String toId) {
        edges.computeIfAbsent(fromId, k -> new HashSet<>()).add(toId);
    }

    /**
     * 获取给定节点的所有邻接节点 ID
     */
    public Set<String> getNeighbors(GraphNode graphNode) {
        return edges.getOrDefault(graphNode.id(), Collections.emptySet());
    }

    /**
     * 根据节点 ID 获取节点
     */
    public GraphNode getGraphNode(String nodeId) {
        return nodes.get(nodeId);
    }
}
