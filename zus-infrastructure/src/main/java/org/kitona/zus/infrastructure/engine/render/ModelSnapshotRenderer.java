package org.kitona.zus.infrastructure.engine.render;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.port.IModelSnapshotRenderer;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * 模型快照渲染器。
 */
@Component
public class ModelSnapshotRenderer implements IModelSnapshotRenderer {

    @Override
    public String render(AuthorizationModelAggregate aggregate) {
        StringBuilder builder = new StringBuilder();
        builder.append("model ").append(aggregate.getModelId()).append('\n');

        for (TypeDefinition typeDefinition : aggregate.getTypeDefinitions().stream()
                .sorted(Comparator.comparingInt(TypeDefinition::getSortOrder))
                .toList()) {
            builder.append("type ").append(typeDefinition.getSubjectType()).append('\n');
            if (typeDefinition.hasRelations()) {
                builder.append("  relations").append('\n');
                for (RelationDefinition relationDefinition : typeDefinition.getRelations().values()) {
                    builder.append("    define ")
                            .append(relationDefinition.relationName())
                            .append(" as ")
                            .append(relationDefinition.rewriteExpression())
                            .append('\n');
                }
            }
        }

        if (!aggregate.getConditionDefinitions().isEmpty()) {
            builder.append('\n');
            for (ConditionDefinition definition : aggregate.getConditionDefinitions()) {
                builder.append("// condition ")
                        .append(definition.getConditionName())
                        .append(": ")
                        .append(definition.getExpression())
                        .append('\n');
            }
        }
        return builder.toString().trim();
    }
}
