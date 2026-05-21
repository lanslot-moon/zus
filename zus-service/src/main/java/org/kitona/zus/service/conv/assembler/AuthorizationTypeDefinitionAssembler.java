package org.kitona.zus.service.conv.assembler;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;
import org.kitona.zus.service.dto.command.CreateModelCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类型定义 Assembler
 *
 * <p>负责 {@link TypeDefinition} 与 DTO/Command 之间的双向转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO/Command 之间的转换。
 * <p>标准模型写入入口采用 Map-keyed schema，relation 名称只来自 Map key。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class AuthorizationTypeDefinitionAssembler {

    private AuthorizationTypeDefinitionAssembler() {
    }

    // ========== Command/Input → RelationDefinition 列表（数据转换） ==========

    /**
     * 从显式关系输入列表转换为 RelationDefinition 列表
     *
     * <p>DDD 规范：Assembler 只负责数据转换，不直接创建领域实体。
     * 此方法将 DTO 层的 Map 结构转换为领域层的 RelationDefinition 列表，
     * 由调用方通过 {@link TypeDefinition#createWithRelations} 工厂方法创建实体。
     *
     * @param relations 关系输入映射，key 为 relation 名称
     * @return RelationDefinition 列表
     */
    public static List<RelationDefinition> toRelationDefinitions(Map<String, CreateModelCommand.RelationInput> relations) {
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        return relations.entrySet().stream()
                .map(entry -> new RelationDefinition(
                        entry.getKey(),
                        entry.getValue().getRewriteExpression(),
                        CollectionUtils.isEmpty(entry.getValue().getAllowedSubjectTypes())
                                ? Collections.emptySet()
                                : new HashSet<>(entry.getValue().getAllowedSubjectTypes())))
                .toList();
    }

    // ========== Entity → DTO ==========

    /**
     * 将类型定义实体转换为 DTO
     *
     * @param entity 类型定义实体
     * @return 类型定义结果 DTO
     */
    public static AuthorizationTypeDefinitionResultDTO toDTO(TypeDefinition entity) {
        if (entity == null) {
            return null;
        }

        AuthorizationTypeDefinitionResultDTO dto = new AuthorizationTypeDefinitionResultDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getSubjectType());

        Map<String, RelationDefinition> relations = entity.getRelations();
        if (!relations.isEmpty()) {
            Map<String, AuthorizationTypeDefinitionResultDTO.RelationDefinitionResultDTO> relationsMap = new LinkedHashMap<>();
            relations.forEach((relationName, relation) -> relationsMap.put(relationName, toRelationResultDTO(relation)));
            dto.setRelations(relationsMap);
        }

        return dto;
    }

    /**
     * 批量将类型定义实体转换为 DTO
     *
     * @param entities 类型定义实体列表
     * @return DTO 列表
     */
    public static List<AuthorizationTypeDefinitionResultDTO> toDTOList(List<TypeDefinition> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(AuthorizationTypeDefinitionAssembler::toDTO)
                .toList();
    }

    private static AuthorizationTypeDefinitionResultDTO.RelationDefinitionResultDTO toRelationResultDTO(
            RelationDefinition relation) {
        AuthorizationTypeDefinitionResultDTO.RelationDefinitionResultDTO dto =
                new AuthorizationTypeDefinitionResultDTO.RelationDefinitionResultDTO();
        dto.setRewriteExpression(relation.rewriteExpression());
        if (CollectionUtils.isNotEmpty(relation.restrictions())) {
            dto.setRestrictions(new ArrayList<>(relation.restrictions()));
        }
        return dto;
    }
}
