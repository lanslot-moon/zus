package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.specification.TupleVisibilityDecision;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 授权求值过程的 Explain 记录适配器。
 *
 * <p>该对象只负责把 evaluator 产生的关键执行事件写入
 * {@link EvaluationTraceCollector}，并集中处理普通 Check 场景下 collector 为空的情况。
 * 这样 {@code PermissionCheckEvaluator} 可以专注于权限语义判断，不需要在主流程中散落
 * trace collector 的空值判断和写入细节。
 */
public final class EvaluationTraceRecorder {

    /**
     * 记录一个求值节点入栈。
     *
     * @param runtime  当前单次求值运行时
     * @param nodeType explain 节点类型
     * @param subject  当前被判断的主体
     * @param object   当前被判断的客体
     * @param relation 当前被判断的关系
     */
    public void enter(EvaluationRuntime runtime, EvaluationNodeType nodeType, Subject subject,
                      ObjectRef object, String relation) {
        EvaluationTraceCollector collector = runtime.traceCollector();
        if (collector != null) {
            collector.enterNode(nodeType, subject, object, relation);
        }
    }

    /**
     * 记录一个求值节点出栈。
     *
     * @param runtime 当前单次求值运行时
     * @param allowed 当前节点是否命中
     * @param reason  当前节点最终原因
     */
    public void leave(EvaluationRuntime runtime, boolean allowed, EvaluationExplainReason reason) {
        EvaluationTraceCollector collector = runtime.traceCollector();
        if (collector != null) {
            collector.leaveNode(allowed, reason);
        }
    }

    /**
     * 标记当前求值节点的中间或终态原因。
     *
     * @param runtime 当前单次求值运行时
     * @param allowed 当前标记是否代表允许
     * @param reason  当前标记原因
     */
    public void mark(EvaluationRuntime runtime, boolean allowed, EvaluationExplainReason reason) {
        EvaluationTraceCollector collector = runtime.traceCollector();
        if (collector != null) {
            collector.markCurrent(allowed, reason);
        }
    }

    /**
     * 记录 tuple 匹配、未匹配或过滤原因。
     *
     * @param runtime 当前单次求值运行时
     * @param tuple   被检查的关系事实
     * @param allowed 该 tuple 是否支持当前授权结论
     * @param reason  tuple 相关原因
     */
    public void recordTuple(EvaluationRuntime runtime, RelationTuple tuple, boolean allowed, EvaluationExplainReason reason) {
        EvaluationTraceCollector collector = runtime.traceCollector();
        if (collector != null) {
            collector.recordTupleDecision(tuple, allowed, reason);
        }
    }

    /**
     * 记录条件表达式的求值结果。
     *
     * @param runtime 当前单次求值运行时
     * @param tuple   绑定条件的关系事实
     * @param allowed 条件是否通过
     * @param reason  条件相关原因
     */
    public void recordCondition(EvaluationRuntime runtime, RelationTuple tuple, boolean allowed, EvaluationExplainReason reason) {
        EvaluationTraceCollector collector = runtime.traceCollector();
        if (collector != null) {
            collector.recordConditionDecision(tuple, allowed, reason);
        }
    }

    /**
     * 记录 tuple 可见性规范产生的 explain 证据。
     *
     * <p>可见性判断由 specification 完成，recorder 只根据 decision 中的证据归类决定写入
     * tuple 证据还是 condition 证据，避免 evaluator 写死可见性原因到 explain 的映射。
     *
     * @param runtime  当前单次求值运行时
     * @param tuple    被判断的关系事实
     * @param decision tuple 可见性规范的结构化判断结果
     */
    public void recordVisibility(EvaluationRuntime runtime, RelationTuple tuple, TupleVisibilityDecision decision) {
        if (!decision.shouldRecord()) {
            return;
        }
        if (decision.traceKind() == TupleVisibilityDecision.TraceKind.CONDITION) {
            recordCondition(runtime, tuple, decision.satisfied(), decision.reason());
            return;
        }
        recordTuple(runtime, tuple, decision.satisfied(), decision.reason());
    }
}
