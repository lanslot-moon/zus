package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.ExclusionNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 差集节点策略。
 *
 * <p>只有左侧成立且右侧不成立时，整个差集表达式才成立。
 */
public final class ExclusionNodeEvaluationStrategy implements RewriteNodeEvaluationStrategy<ExclusionNode> {

    /**
     * 执行差集求值。
     * 该方法用于评估差集操作，即左侧授权成立且右侧授权不成立的情况。
     *
     * @param node             当前要评估的差集节点，包含左右两个子节点
     * @param compiledRelation 已编译的关系表达式
     * @param runtime          评估运行时环境
     * @param subject          主体对象
     * @param object           客体对象引用
     * @param relation         关系名称
     * @param depth            当前评估深度
     * @param support          评估支持接口，提供节点评估功能
     * @return 如果左侧评估为true且右侧评估为false，则返回true；否则返回false
     */
    @Override
    public boolean evaluate(ExclusionNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        // 先判断左侧授权是否成立，再排除命中右侧的情况。
        // 使用support.evaluateNode方法递归评估左节点，如果为false则直接返回false
        // 使用!运算符确保右节点评估为false，即排除命中右侧的情况
        return support.evaluateNode(runtime, compiledRelation, node.left(), subject, object, relation, depth + 1)
                && !support.evaluateNode(runtime, compiledRelation, node.right(), subject, object, relation, depth + 1);
    }
}
