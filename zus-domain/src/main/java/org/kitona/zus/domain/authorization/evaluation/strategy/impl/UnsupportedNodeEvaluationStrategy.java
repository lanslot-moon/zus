package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 默认兜底策略。
 *
 * <p>当策略注册表里没有找到对应实现时，统一返回拒绝，避免未知节点被误判为授权通过。
 */
public final class UnsupportedNodeEvaluationStrategy implements RewriteNodeEvaluationStrategy<RewriteNode> {

    public static final UnsupportedNodeEvaluationStrategy INSTANCE = new UnsupportedNodeEvaluationStrategy();

    private UnsupportedNodeEvaluationStrategy() {
    }

    /**
     * 对未知节点一律返回 false。
     */
    @Override
    public boolean evaluate(RewriteNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        return false;
    }
}
