package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceCollector;
import org.kitona.zus.domain.authorization.evaluation.specification.TupleVisibilitySpecification;
import org.kitona.zus.domain.port.IConditionEvaluator;

/**
 * 单次授权请求的运行时上下文。
 *
 * <p>把一次求值过程中会重复使用的只读上下文集中起来，
 * 避免在递归时反复构建相同的 specification 和时间快照。
 */
public final class EvaluationRuntime {

    /**
     * 编译后的授权模型
     */
    private final CompiledAuthorizationModel model;

    /**
     * 评估请求
     */
    private final EvaluationRequest request;

    /**
     * 递归保护器，防止无限递归
     */
    private final RecursionGuard guard;

    /**
     * 当前时间戳，用于固定时间点评估
     */
    private final long currentTimeMillis;

    /**
     * 元组可见性规范
     */
    private final TupleVisibilitySpecification visibilitySpecification;

    /**
     * 评估追踪收集器
     */
    private final EvaluationTraceCollector traceCollector;

    public EvaluationRuntime(CompiledAuthorizationModel model,
                             EvaluationRequest request,
                             RecursionGuard guard,
                             long currentTimeMillis,
                             IConditionEvaluator conditionEvaluator) {
        this(model, request, guard, currentTimeMillis, conditionEvaluator, null);
    }

    public EvaluationRuntime(CompiledAuthorizationModel model,
                             EvaluationRequest request,
                             RecursionGuard guard,
                             long currentTimeMillis,
                             IConditionEvaluator conditionEvaluator,
                             EvaluationTraceCollector traceCollector) {
        this.model = model;
        this.request = request;
        this.guard = guard;
        this.currentTimeMillis = currentTimeMillis;
        this.traceCollector = traceCollector;
        // 单次请求共享同一份 tuple 可见性规则，避免在递归过程中重复构建。
        this.visibilitySpecification = new TupleVisibilitySpecification(model, request, conditionEvaluator, currentTimeMillis);
    }

    /**
     * 返回当前请求对应的编译模型。
     */
    public CompiledAuthorizationModel model() {
        return model;
    }

    /**
     * 返回当前递归分支使用的求值请求。
     */
    public EvaluationRequest request() {
        return request;
    }

    /**
     * 返回当前请求共享的递归保护器。
     */
    public RecursionGuard guard() {
        return guard;
    }

    /**
     * 返回本次求值固定使用的时间快照。
     */
    public long currentTimeMillis() {
        return currentTimeMillis;
    }

    /**
     * 返回 tuple 可见性判定规则。
     */
    public TupleVisibilitySpecification visibilitySpecification() {
        return visibilitySpecification;
    }

    /**
     * 返回当前 explain 收集器；普通 check 场景下为空。
     */
    public EvaluationTraceCollector traceCollector() {
        return traceCollector;
    }

}
