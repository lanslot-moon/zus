package org.kitona.zus.infrastructure.engine.render;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.port.IModelSnapshotRenderer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 模型快照渲染器。
 */
@Component
public class ModelSnapshotRenderer implements IModelSnapshotRenderer {

    private static final char NEW_LINE = '\n';
    private static final String MODEL_PREFIX = "model ";
    private static final String TYPE_PREFIX = "type ";
    private static final String RELATIONS_BLOCK = "  relations";
    private static final String DEFINE_PREFIX = "    define ";
    private static final String DEFINE_MIDDLE = " as ";
    private static final String CONDITION_PREFIX = "// condition ";
    private static final String CONDITION_MIDDLE = ": ";
    private static final String EMPTY_STRING = "";

    /**
     * 渲染授权模型 DSL 快照。
     *
     * @param aggregate 聚合根
     * @return 执行结果
     */
    @Override
    public String render(AuthorizationModelAggregate aggregate) {
        StringBuilder builder = new StringBuilder();
        appendModelHeader(builder, aggregate);
        appendTypeDefinitions(builder, aggregate);
        appendConditionDefinitions(builder, aggregate.getConditionDefinitions());
        return builder.toString().trim();
    }

    /**
     * 追加模型头部信息。
     *
     * @param builder 字符串构造器
     * @param aggregate 聚合根
     */
    private void appendModelHeader(StringBuilder builder, AuthorizationModelAggregate aggregate) {
        builder.append(MODEL_PREFIX).append(aggregate.getModelId()).append(NEW_LINE);
    }

    /**
     * 追加类型定义列表。
     *
     * @param builder 字符串构造器
     * @param aggregate 聚合根
     */
    private void appendTypeDefinitions(StringBuilder builder, AuthorizationModelAggregate aggregate) {
        List<TypeDefinition> orderedTypes = aggregate.getTypeDefinitions().stream()
                .sorted(Comparator.comparingInt(TypeDefinition::getSortOrder))
                .toList();
        for (TypeDefinition typeDefinition : orderedTypes) {
            appendTypeDefinition(builder, typeDefinition);
        }
    }

    /**
     * 追加单个类型定义。
     *
     * @param builder 字符串构造器
     * @param typeDefinition 类型定义
     */
    private void appendTypeDefinition(StringBuilder builder, TypeDefinition typeDefinition) {
        builder.append(TYPE_PREFIX).append(typeDefinition.getSubjectType()).append(NEW_LINE);
        if (!typeDefinition.hasRelations()) {
            return;
        }
        builder.append(RELATIONS_BLOCK).append(NEW_LINE);
        for (RelationDefinition relationDefinition : typeDefinition.getRelations().values()) {
            appendRelationDefinition(builder, relationDefinition);
        }
    }

    /**
     * 追加关系定义。
     *
     * @param builder 字符串构造器
     * @param relationDefinition 关系定义
     */
    private void appendRelationDefinition(StringBuilder builder, RelationDefinition relationDefinition) {
        builder.append(DEFINE_PREFIX)
                .append(relationDefinition.relationName())
                .append(DEFINE_MIDDLE)
                .append(relationDefinition.rewriteExpression())
                .append(NEW_LINE);
    }

    /**
     * 追加条件定义列表。
     *
     * @param builder 字符串构造器
     * @param conditions 条件定义列表
     */
    private void appendConditionDefinitions(StringBuilder builder, List<ConditionDefinition> conditions) {
        if (conditions.isEmpty()) {
            return;
        }
        builder.append(NEW_LINE);
        for (ConditionDefinition condition : conditions) {
            appendConditionDefinition(builder, condition);
        }
    }

    /**
     * 追加单个条件定义。
     *
     * @param builder 字符串构造器
     * @param condition 条件定义
     */
    private void appendConditionDefinition(StringBuilder builder, ConditionDefinition condition) {
        builder.append(CONDITION_PREFIX)
                .append(condition.getConditionName())
                .append(CONDITION_MIDDLE)
                .append(condition.getExpression())
                .append(NEW_LINE);
    }
}
