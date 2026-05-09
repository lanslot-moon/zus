package org.kitona.zus.domain.authorization.evaluation.specification;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.TupleMatchContext;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.IConditionEvaluator;

/**
 * tuple 可见性规则。
 *
 * <p>用于过滤当前请求上下文下真正“可参与授权计算”的 tuple。
 * 一个 tuple 需要同时满足未过期、条件成立两个要求，才会被 evaluator 使用。
 */
public record TupleVisibilitySpecification(CompiledAuthorizationModel model, EvaluationRequest request,
                                           IConditionEvaluator conditionEvaluator, long currentTimeMillis) {

    /**
     * 判断 tuple 在当前请求上下文下是否不可见。
     */
    public boolean isNotSatisfiedBy(RelationTuple tuple) {
        return evaluate(tuple).isNotSatisfied();
    }

    /**
     * 评估 tuple 在当前请求上下文下的可见性。
     *
     * <p>该方法是 tuple 可见性规则的唯一事实来源。过期过滤、条件定义查找和条件表达式求值
     * 都应该通过这里完成，避免 evaluator 或 strategy 复制同一套规则后产生语义漂移。
     *
     * @param tuple 待判断的关系事实
     * @return 结构化可见性判断结果
     */
    public TupleVisibilityDecision evaluate(RelationTuple tuple) {
        // 先做过期时间过滤，避免无效 tuple 继续参与条件求值。
        if (tuple.isExpired(currentTimeMillis)) {
            return TupleVisibilityDecision.tupleRejected(BusinessEvidenceReason.TUPLE_EXPIRED);
        }
        // 没有条件定义时，该 tuple 只要未过期就可直接参与计算。
        if (!tuple.hasCondition()) {
            return TupleVisibilityDecision.visible();
        }
        // 条件 tuple 需要先找到对应条件定义，再由条件求值器决定是否命中。
        return model.findCondition(tuple.getConditionDefinitionId())
                .map(definition -> conditionEvaluator.evaluate(definition, new TupleMatchContext(request, tuple))
                        ? TupleVisibilityDecision.conditionPassed()
                        : TupleVisibilityDecision.conditionRejected(BusinessEvidenceReason.CONDITION_FAILED))
                .orElse(TupleVisibilityDecision.conditionRejected(BusinessEvidenceReason.CONDITION_DEFINITION_MISSING));
    }
}
