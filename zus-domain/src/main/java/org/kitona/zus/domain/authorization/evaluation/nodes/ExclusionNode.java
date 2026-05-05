package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * 差集节点。
 *
 * @param left 左侧
 * @param right 右侧
 */
public record ExclusionNode(RewriteNode left, RewriteNode right) implements RewriteNode {

    /**
     * 返回 exclusion 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.EXCLUSION;
    }
}
