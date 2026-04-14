package org.kitona.zus.domain.authorization.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 * 条件定义实体（聚合内实体，非聚合根）。
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public final class ConditionDefinition {

    private final Long id;

    private final String conditionName;

    private final String expression;

    private final String parameterSchema;

    private final String description;

    private ConditionDefinition(Long id,
                                String conditionName,
                                String expression,
                                String parameterSchema,
                                String description) {
        this.id = Objects.requireNonNull(id, "conditionDefinitionId 不能为空");
        this.conditionName = Objects.requireNonNull(conditionName, "conditionName must not be null");
        this.expression = Objects.requireNonNull(expression, "expression must not be null");
        this.parameterSchema = parameterSchema;
        this.description = description;
    }

    public static ConditionDefinition create(String conditionName,
                                             String expression,
                                             String parameterSchema,
                                             String description) {
        return new ConditionDefinition(generateConditionDefinitionId(), conditionName, expression, parameterSchema, description);
    }

    public static ConditionDefinition reconstitute(Long id,
                                                   String conditionName,
                                                   String expression,
                                                   String parameterSchema,
                                                   String description) {
        return new ConditionDefinition(id, conditionName, expression, parameterSchema, description);
    }

    private static long generateConditionDefinitionId() {
        long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        return id == 0L ? 1L : id;
    }
}
