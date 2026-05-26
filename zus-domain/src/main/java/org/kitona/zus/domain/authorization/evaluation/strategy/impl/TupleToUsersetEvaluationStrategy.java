package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceCollector;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.TupleToUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.Optional;

/**
 * tuple-to-userset 节点策略。
 *
 * <p>先通过 tuple relation 找到中间对象，再把目标 subject 投影到中间对象的 computed relation 上继续递归。
 */
public final class TupleToUsersetEvaluationStrategy implements RewriteNodeEvaluationStrategy<TupleToUsersetNode> {

    /**
     * 执行 TTU (TupleToUserset) 求值。
     * <p>
     * 该方法通过 tuple relation 找到中间对象，然后使用 computed relation 递归验证目标权限。
     *
     * @param node             TupleToUserset 节点，包含 tuple relation 和 computed relation 信息
     * @param compiledRelation 编译后的关系信息
     * @param runtime          评估运行时环境，包含可见性规范等
     * @param subject          待验证的主体
     * @param object           待验证的对象
     * @param relation         关系名称
     * @param depth            当前递归深度
     * @param support          重写节点评估支持工具，提供查找和评估功能
     * @return 如果找到满足条件的路径返回 true，否则返回 false
     */
    @Override
    public boolean evaluate(TupleToUsersetNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        // TTU 先顺着 tuple relation 找到中间对象，再用 computed relation 递归验证目标权限。
        for (RelationTuple link : support.findDirectTuples(runtime, object, node.tupleRelation())) {
            // userset / wildcard 不能作为 TTU 中间对象，且不可见 tuple 必须提前过滤。
            if (runtime.visibilitySpecification().isNotSatisfiedBy(link) || link.hasUsersetSubject() || link.hasWildcardSubject()) {
                continue;
            }

            ObjectRef linkedObject = ObjectRef.of(link.getSubjectType(), link.getSubjectId());
            if (support.evaluateRelation(runtime, subject, linkedObject, node.computedRelation(), depth + 1)) {
                // TTU 的 link tuple 是“当前对象 -> 中间对象”的传播边证据。
                // relation/rewrite 节点可由 evaluator 统一记录，但这条边只在 TTU 策略内部可见。
                // 这里必须显式写入 explain 树，否则授权路径会缺少从 object 跳转到 linkedObject 的原因。
                EvaluationTraceCollector collector = runtime.traceCollector();
                Optional.ofNullable(collector).ifPresent(item -> item.recordTupleDecision(link, true, BusinessEvidenceReason.TUPLE_TO_USERSET_LINK_MATCHED));
                return true;
            }
        }
        return false;
    }
}
