package org.kitona.zus.business.entity.bo;

import lombok.Getter;

import java.util.*;

/**
 * 有向图
 */
@Getter
public class DirectedGraph {

    /**
     * 有向图的节点 key为 document#viewer
     */
    private final Map<String, GraphNode> nodes = new HashMap<>();

    /**
     * 有向图的边，key为 document#viewer, 即 resourceType#relation
     */
    private final Map<String, Set<String>> edges = new HashMap<>();

    public GraphNode addNode(String id, String label, NodeType type) {
        return nodes.computeIfAbsent(id, k -> new GraphNode(id, label, type));
    }

    public void addEdge(String fromId, String toId) {
        edges.computeIfAbsent(fromId, k -> new HashSet<>()).add(toId);
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
