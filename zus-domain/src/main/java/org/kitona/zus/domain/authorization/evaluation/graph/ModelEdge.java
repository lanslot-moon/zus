package org.kitona.zus.domain.authorization.evaluation.graph;

/**
 * 授权模型图中的有向边。
 */
public record ModelEdge(String from, String to, GraphEdgeType type) {
}
