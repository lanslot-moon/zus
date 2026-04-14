package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.UnionNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 并集节点策略。
 *
 * <p>只要任意子节点成立，整个并集表达式就成立。
 */
public final class UnionNodeEvaluationStrategy implements RewriteNodeEvaluationStrategy<UnionNode> {

    /**
     * 执行并集求值。
     * 该方法用于评估并集操作，当任一子节点评估为true时，整个并集表达式即为true。
     *
     * @param node             当前待评估的并集节点
     * @param compiledRelation 已编译的关系信息
     * @param runtime          运行时环境
     * @param subject          主体对象
     * @param object           客体对象引用
     * @param relation         关系名称
     * @param depth            当前递归深度
     * @param support          提供节点评估支持的辅助类
     * @return 如果任一子节点评估为true则返回true，否则返回false
     */
    @Override
    public boolean evaluate(UnionNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        for (RewriteNode child : node.children()) {
            // 并集场景下，一旦有子节点命中就可以立即短路返回。
            if (support.evaluateNode(runtime, compiledRelation, child, subject, object, relation, depth + 1)) {
                return true;
            }
        }
        return false;
    }
}
