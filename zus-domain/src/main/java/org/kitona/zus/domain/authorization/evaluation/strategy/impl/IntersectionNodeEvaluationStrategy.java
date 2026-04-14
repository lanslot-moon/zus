package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.IntersectionNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 交集节点策略。
 *
 * <p>所有子节点都成立时，交集表达式才成立。
 */
public final class IntersectionNodeEvaluationStrategy implements RewriteNodeEvaluationStrategy<IntersectionNode> {

    /**
     * 执行交集求值。
     */
    @Override
    public boolean evaluate(IntersectionNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        for (RewriteNode child : node.children()) {
            // 任一子节点失败，整个交集立即失败。
            if (!support.evaluateNode(runtime, compiledRelation, child, subject, object, relation, depth + 1)) {
                return false;
            }
        }
        return true;
    }
}
