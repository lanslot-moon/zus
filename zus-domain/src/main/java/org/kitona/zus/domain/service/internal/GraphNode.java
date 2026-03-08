package org.kitona.zus.domain.service.internal;

/**
 * 图节点（值对象）
 * <p>
 * 表示授权模型图中的一个节点，可以是资源类型或类型+关系的组合。
 *
 * @param id    节点 id，例如 document#viewer
 * @param label 节点标签
 * @param type  节点类型
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record GraphNode(String id, String label, NodeType type) {
}
