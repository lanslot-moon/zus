package org.kitona.zus.service.factory;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.service.assembler.AuthorizationTypeDefinitionAssembler;
import org.kitona.zus.service.dto.command.CreateModelCommand;

import java.util.List;

/**
 * 授权模型草稿工厂。
 *
 * <p>应用层通过该工厂一次性构建结构化模型聚合，
 * 避免控制器和应用服务散落着模型装配细节。
 */
public final class AuthorizationModelDraftFactory {

    private AuthorizationModelDraftFactory() {
    }

    public static AuthorizationModelAggregate create(CreateModelCommand command) {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.createWithGeneratedId(
                command.getStoreId(),
                command.getSchemaVersion(),
                command.getDescription()
        );
        aggregate.addTypeDefinitions(buildTypeDefinitions(command.getTypeDefinitions()));
        aggregate.replaceConditionDefinitions(buildConditionDefinitions(command.getConditions()));
        return aggregate;
    }

    private static List<TypeDefinition> buildTypeDefinitions(List<CreateModelCommand.TypeDefinitionInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> TypeDefinition.createWithRelations(
                        input.getType(),
                        AuthorizationTypeDefinitionAssembler.toRelationDefinitions(input.getRelations())))
                .toList();
    }

    private static List<ConditionDefinition> buildConditionDefinitions(List<CreateModelCommand.ConditionDefinitionInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> ConditionDefinition.create(
                        input.getName(),
                        input.getExpression(),
                        input.getParameterSchema(),
                        input.getDescription()))
                .toList();
    }
}
