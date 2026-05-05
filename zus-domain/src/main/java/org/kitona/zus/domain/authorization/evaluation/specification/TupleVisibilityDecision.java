package org.kitona.zus.domain.authorization.evaluation.specification;

import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;

/**
 * tuple 可见性规则的结构化判断结果。
 *
 * <p>该值对象只描述 {@link TupleVisibilitySpecification} 的判断结论：
 * tuple 是否可参与授权计算，以及如果需要 explain，应当记录到 tuple 证据还是 condition 证据。
 * 它不直接依赖 collector，也不执行记录动作，从而避免 specification 和 explain 运行时对象耦合。
 *
 * @param satisfied tuple 是否满足当前请求上下文下的可见性规则
 * @param reason    需要记录 explain 时使用的结构化原因；无需记录时为空
 * @param traceKind explain 证据归类；无需记录时为 {@link TraceKind#NONE}
 */
public record TupleVisibilityDecision(boolean satisfied,
                                      EvaluationExplainReason reason,
                                      TraceKind traceKind) {

    /**
     * 表示 tuple 可见且无需额外记录可见性 explain。
     */
    private static final TupleVisibilityDecision VISIBLE = new TupleVisibilityDecision(true, null, TraceKind.NONE);

    /**
     * 表示 tuple 绑定的条件通过，需要记录 condition explain。
     */
    private static final TupleVisibilityDecision CONDITION_PASSED =
            new TupleVisibilityDecision(true, EvaluationExplainReason.CONDITION_PASSED, TraceKind.CONDITION);

    /**
     * 创建无条件且可见的判断结果。
     *
     * @return 可见且无需额外 explain 的结果
     */
    public static TupleVisibilityDecision visible() {
        return VISIBLE;
    }

    /**
     * 创建条件通过的判断结果。
     *
     * @return 可见且需要记录 condition 通过原因的结果
     */
    public static TupleVisibilityDecision conditionPassed() {
        return CONDITION_PASSED;
    }

    /**
     * 创建 tuple 层不可见的判断结果。
     *
     * @param reason tuple 不可见的结构化原因
     * @return 不可见且应记录到 tuple 证据的结果
     */
    public static TupleVisibilityDecision tupleRejected(EvaluationExplainReason reason) {
        return new TupleVisibilityDecision(false, reason, TraceKind.TUPLE);
    }

    /**
     * 创建 condition 层不可见的判断结果。
     *
     * @param reason condition 不可见的结构化原因
     * @return 不可见且应记录到 condition 证据的结果
     */
    public static TupleVisibilityDecision conditionRejected(EvaluationExplainReason reason) {
        return new TupleVisibilityDecision(false, reason, TraceKind.CONDITION);
    }

    /**
     * 判断 tuple 是否不满足可见性规则。
     *
     * @return 不满足可见性规则时返回 {@code true}
     */
    public boolean isNotSatisfied() {
        return !satisfied;
    }

    /**
     * 判断该结果是否需要写入 explain。
     *
     * @return 需要记录 explain 时返回 {@code true}
     */
    public boolean shouldRecord() {
        return traceKind != TraceKind.NONE;
    }

    /**
     * tuple 可见性 explain 的证据归类。
     */
    public enum TraceKind {
        /**
         * 不需要记录额外 explain。
         */
        NONE,

        /**
         * 记录为 tuple 证据。
         */
        TUPLE,

        /**
         * 记录为 condition 证据。
         */
        CONDITION
    }
}
