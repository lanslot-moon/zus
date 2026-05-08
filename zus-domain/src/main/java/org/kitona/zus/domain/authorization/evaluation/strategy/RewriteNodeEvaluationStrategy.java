package org.kitona.zus.domain.authorization.evaluation.strategy;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * rewrite 节点求值策略。
 *
 * @param <T> 节点类型
 */
@SuppressWarnings("java:S107")
public interface RewriteNodeEvaluationStrategy<T extends RewriteNode> {

    /**
     * 求值当前 rewrite 节点。
     */
    boolean evaluate(T node, CompiledRelation compiledRelation, EvaluationRuntime runtime, Subject subject,
                     ObjectRef objectRef, String relation, int depth, RewriteNodeEvaluationSupport support);
}
