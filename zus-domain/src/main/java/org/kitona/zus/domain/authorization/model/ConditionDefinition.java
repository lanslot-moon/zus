package org.kitona.zus.domain.authorization.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

/**
 * 条件定义实体（聚合内实体，非聚合根）。
 * <p>
 * 该类用于表示系统中的条件定义，包含条件的名称、表达式、参数模式等核心信息。
 * 采用不可变设计模式，所有字段均为final，通过工厂方法创建实例。
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public final class ConditionDefinition {

    /**
     * 条件定义的唯一标识符
     */
    private final Long id;

    /**
     * 条件名称，用于标识和描述条件
     */
    private final String conditionName;

    /**
     * 条件表达式，定义条件的具体逻辑
     */
    private final String expression;

    /**
     * 参数模式，描述条件表达式的参数结构
     */
    private final String parameterSchema;

    /**
     * 条件描述，提供关于条件的详细说明
     */
    private final String description;

    /**
     * 私有构造方法，用于创建条件定义实例
     *
     * @param id              条件定义ID，不能为空
     * @param conditionName   条件名称，不能为空
     * @param expression      条件表达式，不能为空
     * @param parameterSchema 参数模式，可以为空
     * @param description     条件描述，可以为空
     * @throws NullPointerException 如果id、conditionName或expression为空
     */
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

    /**
     * 创建新的条件定义实例
     * <p>
     * 自动生成唯一ID并创建条件定义实例
     * </p>
     *
     * @param conditionName   条件名称，不能为空
     * @param expression      条件表达式，不能为空
     * @param parameterSchema 参数模式，可以为空
     * @param description     条件描述，可以为空
     * @return 新创建的条件定义实例
     * @throws NullPointerException 如果conditionName或expression为空
     */
    public static ConditionDefinition create(String conditionName,
                                             String expression,
                                             String parameterSchema,
                                             String description) {
        return new ConditionDefinition(generateConditionDefinitionId(), conditionName, expression, parameterSchema, description);
    }

    /**
     * 从持久化存储重建条件定义实例
     * <p>
     * 用于从数据库或其他持久化存储中恢复条件定义对象
     * </p>
     *
     * @param id              条件定义ID，不能为空
     * @param conditionName   条件名称，不能为空
     * @param expression      条件表达式，不能为空
     * @param parameterSchema 参数模式，可以为空
     * @param description     条件描述，可以为空
     * @return 重建的条件定义实例
     * @throws NullPointerException 如果id、conditionName或expression为空
     */
    public static ConditionDefinition reconstitute(Long id,
                                                   String conditionName,
                                                   String expression,
                                                   String parameterSchema,
                                                   String description) {
        return new ConditionDefinition(id, conditionName, expression, parameterSchema, description);
    }

    /**
     * 生成唯一的条件定义ID
     * <p>
     * 使用UUID生成正长整型ID，确保ID为正数且不为0
     * </p>
     *
     * @return 生成的唯一ID，保证为正数且不为0
     */
    private static long generateConditionDefinitionId() {
        long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        return id == 0L ? 1L : id;
    }
}
