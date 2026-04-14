package org.kitona.zus.domain.authorization.evaluation.runtime;

import java.util.function.Supplier;

/**
 * 递归求值模板。
 *
 * <p>统一处理深度保护、循环检测和 memoization，
 * 让具体领域服务专注于“某个关系应该如何求值”。
 */
public final class RecursiveEvaluationTemplate {

    /**
     * 执行一次带递归保护的求值动作。
     *
     * <p>执行顺序固定为：深度检查 -> memo 命中 -> 环检测 -> 执行真实求值 -> 回填 memo。
     * 这样调用方只需要提供具体的求值逻辑，不需要重复写这些横切控制。
     */
    public boolean execute(EvaluationRuntime runtime, EvaluationMemoKey memoKey, int depth, Supplier<Boolean> evaluator) {
        // 检查当前递归深度是否超过最大允许深度，防止无限递归
        if (depth > runtime.guard().maxDepth()) {
            return false;
        }

        // 检查是否已经计算过（memoization），如果已经计算过则直接返回缓存结果
        Boolean memoized = runtime.guard().getMemo(memoKey);
        if (memoized != null) {
            return memoized;
        }
        // 检查当前节点是否正在访问路径中，用于检测循环引用
        if (runtime.guard().isVisiting(memoKey)) {
            return false;
        }

        // 先登记访问路径，再执行真实求值，避免递归回边再次进入同一个节点。
        runtime.guard().enter(memoKey);
        boolean result = evaluator.get();
        // 求值结束后及时移出访问路径，并把结果写入 memo，供后续分支复用。
        runtime.guard().exit(memoKey);
        runtime.guard().putMemo(memoKey, result);
        return result;
    }
}
