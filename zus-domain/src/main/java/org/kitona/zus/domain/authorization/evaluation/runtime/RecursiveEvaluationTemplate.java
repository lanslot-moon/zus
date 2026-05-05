package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceRecorder;

import java.util.function.BooleanSupplier;

/**
 * 递归求值模板。
 *
 * <p>统一处理深度保护、循环检测和 memoization，
 * 让具体领域服务专注于“某个关系应该如何求值”。
 */
public final class RecursiveEvaluationTemplate {

    /**
     * Explain 记录适配器，用于记录递归保护、memo 和循环检测等模板层原因。
     */
    private final EvaluationTraceRecorder traceRecorder = new EvaluationTraceRecorder();

    /**
     * 执行一次带递归保护的求值动作。
     *
     * <p>执行顺序固定为：深度检查 -> memo 命中 -> 环检测 -> 执行真实求值 -> 回填 memo。
     * 这样调用方只需要提供具体的求值逻辑，不需要重复写这些横切控制。
     */
    public boolean execute(EvaluationRuntime runtime, EvaluationMemoKey memoKey, int depth, BooleanSupplier evaluator) {
        // 检查当前递归深度是否超过最大允许深度，防止无限递归
        if (depth > runtime.guard().maxDepth()) {
            mark(runtime, false, EvaluationExplainReason.DEPTH_LIMIT_EXCEEDED);
            return false;
        }

        // 检查是否已经计算过（memoization），如果已经计算过则直接返回缓存结果
        Boolean memoized = runtime.guard().getMemo(memoKey);
        if (memoized != null) {
            mark(runtime, memoized, memoized ? EvaluationExplainReason.MEMO_ALLOWED : EvaluationExplainReason.MEMO_DENIED);
            return memoized;
        }

        // 检查当前节点是否正在访问路径中，用于检测循环引用
        if (runtime.guard().isVisiting(memoKey)) {
            mark(runtime, false, EvaluationExplainReason.CYCLE_DETECTED);
            return false;
        }

        // 先登记访问路径，再执行真实求值，避免递归回边再次进入同一个节点。
        runtime.guard().enter(memoKey);
        boolean result = evaluator.getAsBoolean();
        // 求值结束后及时移出访问路径，并把结果写入 memo，供后续分支复用。
        runtime.guard().exit(memoKey);
        runtime.guard().putMemo(memoKey, result);
        return result;
    }

    /**
     * 标记评估结果的方法
     *
     * @param runtime 评估运行时环境，包含评估过程中的各种信息和工具
     * @param allowed 是否允许通过评估的标志
     * @param reason  评估结果的解释原因
     */
    private void mark(EvaluationRuntime runtime, boolean allowed, EvaluationExplainReason reason) {
        traceRecorder.mark(runtime, allowed, reason);
    }
}
