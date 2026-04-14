package org.kitona.zus.domain.authorization.evaluation.nodes;

/**
 * computed userset 节点。
 *
 * @param resourceType 目标资源类型，为空表示沿用当前类型
 * @param relationName 目标关系
 */
public record ComputedUsersetNode(String resourceType, String relationName) implements RewriteNode {
}
