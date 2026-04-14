package org.kitona.zus.domain.authorization.evaluation.nodes;

/**
 * 直接关系引用。
 *
 * @param relationName 目标关系
 */
public record DirectRelationReferenceNode(String relationName) implements RewriteNode {
}
