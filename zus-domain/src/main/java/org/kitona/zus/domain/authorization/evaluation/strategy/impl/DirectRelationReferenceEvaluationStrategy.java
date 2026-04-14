package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.nodes.DirectRelationReferenceNode;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 直接关系引用策略。
 *
 * <p>它不改变对象和主体，只是把当前判断转发到同一对象的另一个 relation 上。
 */
public final class DirectRelationReferenceEvaluationStrategy
        implements RewriteNodeEvaluationStrategy<DirectRelationReferenceNode> {

    /**
     * 执行直接关系引用递归。
     * 该方法是实现直接关系引用节点评估的核心方法，通过调用支持类的评估方法来完成递归评估。
     *
     * @param node             当前正在评估的直接关系引用节点
     * @param compiledRelation 已编译的关系信息
     * @param runtime          评估运行时环境，提供必要的上下文信息
     * @param subject          关系中的主体对象
     * @param object           关系中的客体对象
     * @param relation         当前要评估的关系名称
     * @param depth            当前递归深度，用于防止无限递归
     * @param support          提供评估功能的支持类，包含实际的评估逻辑
     * @return 返回关系评估的结果，true表示关系成立，false表示关系不成立
     */
    @Override
    public boolean evaluate(DirectRelationReferenceNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        return support.evaluateRelation(runtime, subject, object, node.relationName(), depth + 1);
    }
}
