package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * tuple to userset 节点。
 *
 * @param tupleRelation 链接关系
 * @param computedRelation 目标关系
 */
public record TupleToUsersetNode(String tupleRelation, String computedRelation) implements RewriteNode {

    /**
     * 返回 tuple-to-userset 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.TUPLE_TO_USERSET;
    }
}
