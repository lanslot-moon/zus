package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

/**
 * 条件求值解释详情。
 *
 * @param conditionDefinitionId 条件定义 ID
 * @param conditionName         条件名称
 * @param passed                条件是否通过
 */
public record ConditionExplainDetail(Long conditionDefinitionId, String conditionName, boolean passed) {

    /**
     * 从 tuple 条件绑定构造解释详情。
     */
    public static ConditionExplainDetail of(RelationTuple tuple, boolean passed) {
        return new ConditionExplainDetail(tuple.getConditionDefinitionId(), tuple.getConditionName(), passed);
    }
}
