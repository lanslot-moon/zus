package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

import java.util.List;

/**
 * 并集节点。
 *
 * @param children 子节点
 */
public record UnionNode(List<RewriteNode> children) implements RewriteNode {

    /**
     * 返回 union 节点在 explain 树中的类型。
     */
    @Override
    public EvaluationNodeType explainNodeType() {
        return EvaluationNodeType.UNION;
    }
}
