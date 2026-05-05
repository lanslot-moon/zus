package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * self 节点。
 */
public record SelfNode() implements RewriteNode {

    /**
     * 返回 self 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.SELF;
    }
}
