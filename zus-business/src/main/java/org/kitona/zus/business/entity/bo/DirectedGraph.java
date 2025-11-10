package org.kitona.zus.business.entity.bo;

import java.util.*;

/**
 * 有向图
 */
public class DirectedGraph {

    /**
     * 有向图的节点 key为 document#viewer
     */
    private final Map<String, GraphNode> nodes = new HashMap<>();

    /**
     * 有向图的边，key为 document#viewer, 即 resourceType#relation
     */
    private final Map<String, Set<String>> edges = new HashMap<>();


    /**
     * 向图中添加一个节点的方法
     * 如果指定ID的节点已存在，则返回已存在的节点；否则创建并返回一个新节点
     *
     * @param id    节点的唯一标识符
     * @param label 节点的显示标签
     * @param type  节点的类型枚举值
     * @return 返回已存在或新创建的GraphNode对象
     */
    public GraphNode addNode(String id, String label, NodeType type) {
        return nodes.computeIfAbsent(id, k -> new GraphNode(id, label, type));
    }

    /**
     * 添加一条从fromId到toId的有向边
     *
     * @param fromId 边的起始节点ID
     * @param toId   边的目标节点ID
     */
    public void addEdge(String fromId, String toId) {
        edges.computeIfAbsent(fromId, k -> new HashSet<>()).add(toId);
    }

    /**
     * 获取给定图节点的所有相邻节点
     *
     * @param graphNode 需要查询相邻节点的图节点
     * @return 返回一个包含所有相邻节点标识符的集合，如果没有相邻节点则返回空集合
     */
    public Set<String> getNeighbors(GraphNode graphNode) {
        return edges.getOrDefault(graphNode.id(), Collections.emptySet());
    }

    /**
     * 根据节点ID获取图中的节点
     *
     * @param nodeId 要查找的节点ID
     * @return 对应ID的GraphNode对象，如果不存在则返回null
     */
    public GraphNode getGraphNode(String nodeId) {
        return nodes.get(nodeId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var entry : edges.entrySet()) {
            for (String target : entry.getValue()) {
                sb.append(entry.getKey()).append(" -> ").append(target).append("\n");
            }
        }
        return sb.toString();
    }
}
