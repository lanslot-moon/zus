package org.kitona.zus.domain.authorization.evaluation.nodes;

/**
 * tuple to userset 节点。
 *
 * @param tupleRelation 链接关系
 * @param computedRelation 目标关系
 */
public record TupleToUsersetNode(String tupleRelation, String computedRelation) implements RewriteNode {
}
