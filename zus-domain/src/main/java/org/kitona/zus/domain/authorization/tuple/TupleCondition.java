package org.kitona.zus.domain.authorization.tuple;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * tuple 条件值对象
 *
 * @param conditionDefinitionId 条件定义ID
 * @param conditionName    条件名称
 * @param conditionContext 条件上下文
 */
public record TupleCondition(Long conditionDefinitionId, String conditionName, String conditionContext) {

    public static final TupleCondition EMPTY = new TupleCondition(null, null, null);

    public static TupleCondition of(Long conditionDefinitionId, String conditionName, String conditionContext) {
        if (conditionDefinitionId == null
                && StringUtils.isBlank(conditionName)
                && StringUtils.isBlank(conditionContext)) {
            return EMPTY;
        }
        if (conditionDefinitionId == null) {
            throw new IllegalArgumentException("conditionDefinitionId 不能为空");
        }
        return new TupleCondition(conditionDefinitionId, conditionName, conditionContext);
    }

    public boolean isEmpty() {
        return conditionDefinitionId == null
                && StringUtils.isBlank(conditionName)
                && StringUtils.isBlank(conditionContext);
    }
}
