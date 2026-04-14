package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.evaluation.runtime.TupleMatchContext;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;

/**
 * 条件求值端口。
 */
public interface IConditionEvaluator {

    /**
     * 评估给定的条件定义是否在指定的上下文中成立
     *
     * @param definition 要评估的条件定义对象，包含条件的具体规则和约束
     * @param context    包含评估所需的数据和上下文信息的元组匹配上下文对象
     * @return 如果条件在给定上下文中成立则返回true，否则返回false
     */
    boolean evaluate(ConditionDefinition definition, TupleMatchContext context);
}
