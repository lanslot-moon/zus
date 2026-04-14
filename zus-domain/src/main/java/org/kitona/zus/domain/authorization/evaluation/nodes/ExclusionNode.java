package org.kitona.zus.domain.authorization.evaluation.nodes;

/**
 * 差集节点。
 *
 * @param left 左侧
 * @param right 右侧
 */
public record ExclusionNode(RewriteNode left, RewriteNode right) implements RewriteNode {
}
