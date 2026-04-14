package org.kitona.zus.domain.authorization.evaluation.nodes;

import java.util.List;

/**
 * 交集节点。
 *
 * @param children 子节点
 */
public record IntersectionNode(List<RewriteNode> children) implements RewriteNode {
}
