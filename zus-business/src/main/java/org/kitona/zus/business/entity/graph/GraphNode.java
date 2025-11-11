package org.kitona.zus.business.entity.graph;


/**
 * 图节点
 *
 * @param id    节点id document#viewer
 * @param label 节点标签
 * @param type  节点类型
 */
public record GraphNode(String id, String label, NodeType type) {
}
