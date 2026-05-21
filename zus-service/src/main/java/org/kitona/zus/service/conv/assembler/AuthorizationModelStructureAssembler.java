package org.kitona.zus.service.conv.assembler;

import org.kitona.zus.domain.authorization.model.AuthorizationModelStructure;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.service.dto.command.CreateModelCommand;

import java.util.List;
import java.util.Map;

/**
 * 授权模型结构 Assembler：Command 结构化输入 → {@link AuthorizationModelStructure}。
 */
public final class AuthorizationModelStructureAssembler {

    private AuthorizationModelStructureAssembler() {
    }

    /**
     * 从创建模型命令构建领域结构值对象。
     */
    public static AuthorizationModelStructure fromCreateModelCommand(CreateModelCommand command) {
        if (command == null) {
            return AuthorizationModelStructure.empty();
        }
        return AuthorizationModelStructure.fromTypesAndConditions(
                buildTypes(command.getTypes()),
                buildConditions(command.getConditions()));
    }

    private static List<TypeDefinition> buildTypes(Map<String, CreateModelCommand.TypeDefinitionInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return List.of();
        }
        return inputs.entrySet().stream()
                .map(entry -> TypeDefinition.createWithRelations(
                        entry.getKey(),
                        AuthorizationTypeDefinitionAssembler.toRelationDefinitions(entry.getValue().getRelations())))
                .toList();
    }

    private static List<ConditionDefinition> buildConditions(Map<String, CreateModelCommand.ConditionDefinitionInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return List.of();
        }
        return inputs.entrySet().stream()
                .map(entry -> ConditionDefinition.create(
                        entry.getKey(),
                        entry.getValue().getExpression(),
                        entry.getValue().getParameterSchema(),
                        entry.getValue().getDescription()))
                .toList();
    }
}
