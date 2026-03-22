package org.kitona.zus.domain.valueobject;

import org.apache.commons.lang3.StringUtils;

/**
 * tuple 条件值对象
 *
 * @param conditionName    条件名称
 * @param conditionContext 条件上下文
 */
public record TupleCondition(String conditionName, String conditionContext) {

    public static final TupleCondition EMPTY = new TupleCondition(null, null);

    public static TupleCondition of(String conditionName, String conditionContext) {
        if (StringUtils.isBlank(conditionName) && StringUtils.isBlank(conditionContext)) {
            return EMPTY;
        }
        return new TupleCondition(conditionName, conditionContext);
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(conditionName) && StringUtils.isBlank(conditionContext);
    }
}
