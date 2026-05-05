package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * 直接关系引用。
 *
 * @param relationName 目标关系
 */
public record DirectRelationReferenceNode(String relationName) implements RewriteNode {

    /**
     * 返回 direct relation reference 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.DIRECT_RELATION_REFERENCE;
    }
}
