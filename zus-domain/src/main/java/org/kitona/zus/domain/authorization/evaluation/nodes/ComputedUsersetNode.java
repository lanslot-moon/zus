package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * computed userset 节点。
 *
 * @param resourceType 目标资源类型，为空表示沿用当前类型
 * @param relationName 目标关系
 */
public record ComputedUsersetNode(String resourceType, String relationName) implements RewriteNode {

    /**
     * 返回 computed userset 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.COMPUTED_USERSET;
    }
}
