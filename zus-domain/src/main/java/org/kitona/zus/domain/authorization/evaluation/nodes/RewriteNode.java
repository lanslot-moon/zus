package org.kitona.zus.domain.authorization.evaluation.nodes;

/**
 * 重写表达式节点。
 */
public sealed interface RewriteNode permits SelfNode, DirectRelationReferenceNode, ComputedUsersetNode,
        TupleToUsersetNode, UnionNode, IntersectionNode, ExclusionNode {
}
