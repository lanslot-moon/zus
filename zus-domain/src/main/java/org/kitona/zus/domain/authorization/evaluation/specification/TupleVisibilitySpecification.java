package org.kitona.zus.domain.authorization.evaluation.specification;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
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
        // 先做过期时间过滤，避免无效 tuple 继续参与条件求值。
        if (tuple.isExpired(currentTimeMillis)) {
            return true;
        }
        // 没有条件定义时，该 tuple 只要未过期就可直接参与计算。
        if (!tuple.hasCondition()) {
            return false;
        }
        // 条件 tuple 需要先找到对应条件定义，再由条件求值器决定是否命中。
        return model.findCondition(tuple.getConditionDefinitionId())
                .map(definition -> !conditionEvaluator.evaluate(definition, new TupleMatchContext(request, tuple)))
                .orElse(true);
    }
}
