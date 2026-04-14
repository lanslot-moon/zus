package org.kitona.zus.domain.authorization.evaluation.compiled;

import org.kitona.zus.domain.authorization.model.ConditionDefinition;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 已编译授权模型。
 *
 * @param relations  关系定义，key=type#relation
 * @param conditions 条件定义，key=conditionDefinitionId
 */
public record CompiledAuthorizationModel(Map<String, CompiledRelation> relations,
                                         Map<Long, ConditionDefinition> conditions) {

    /**
     * 构造不可变的编译模型视图，避免运行时修改编译结果。
     */
    public CompiledAuthorizationModel {
        relations = relations == null ? Collections.emptyMap() : Collections.unmodifiableMap(relations);
        conditions = conditions == null ? Collections.emptyMap() : Collections.unmodifiableMap(conditions);
    }

    /**
     * 按 {@code type#relation} 键查找已编译关系。
     */
    public Optional<CompiledRelation> findRelation(String type, String relation) {
        return Optional.ofNullable(relations.get(type + "#" + relation));
    }

    /**
     * 按条件定义标识查找条件实体。
     */
    public Optional<ConditionDefinition> findCondition(Long conditionDefinitionId) {
        return Optional.ofNullable(conditions.get(conditionDefinitionId));
    }
}
