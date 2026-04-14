package org.kitona.zus.domain.authorization.evaluation.nodes;

import java.util.List;

/**
 * 并集节点。
 *
 * @param children 子节点
 */
public record UnionNode(List<RewriteNode> children) implements RewriteNode {
}
