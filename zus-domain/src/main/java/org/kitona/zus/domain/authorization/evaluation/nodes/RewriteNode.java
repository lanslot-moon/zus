package org.kitona.zus.domain.authorization.evaluation.nodes;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * 重写表达式节点。
 */
public sealed interface RewriteNode permits SelfNode, DirectRelationReferenceNode, ComputedUsersetNode,
        TupleToUsersetNode, UnionNode, IntersectionNode, ExclusionNode {

    /**
     * 返回当前 rewrite 节点在 explain 树中的节点类型。
     *
     * <p>节点类型属于 rewrite 节点自身的结构元信息，由节点实现类直接声明，
     * 避免 evaluator 通过 {@code instanceof} 维护一份容易漂移的映射表。
     *
     * @return explain 树节点类型
     */
    EvaluationNodeType explainNodeType();
}
