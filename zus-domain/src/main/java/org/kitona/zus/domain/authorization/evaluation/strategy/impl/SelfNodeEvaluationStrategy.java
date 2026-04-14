package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.SelfNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * self 节点策略。
 *
 * <p>把当前节点交还给 evaluator 的 direct tuple 判定逻辑处理。
 */
public final class SelfNodeEvaluationStrategy implements RewriteNodeEvaluationStrategy<SelfNode> {

    /**
     * 执行 self 判定。
     * 这是一个重写的方法，用于评估 self 节点。
     *
     * @param node             当前正在评估的 SelfNode 节点
     * @param compiledRelation 已编译的关系信息
     * @param runtime          运行时环境，提供必要的上下文和工具方法
     * @param subject          评估的主体
     * @param object           评估的客体
     * @param relation         当前关系名称
     * @param depth            当前评估的递归深度，用于防止循环引用
     * @param support          提供节点评估支持的接口，包含各种评估工具方法
     * @return 返回 self 判定的结果，true 表示满足 self 条件，false 表示不满足
     */
    @Override
    public boolean evaluate(SelfNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        // 调用支持类的方法执行实际的 self 评估
        return support.evaluateSelf(compiledRelation, subject, object, relation, runtime);
    }
}
