package org.kitona.zus.service.assembler;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.service.dto.response.AuthorizationRelationResultDTO;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;
import org.kitona.zus.service.dto.command.CreateModelCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类型定义 Assembler
 *
 * <p>负责 {@link TypeDefinition} 与 DTO/Command 之间的双向转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO/Command 之间的转换。
 * <p>类型定义的结构化输入（type + relations + relationRestrictions）在
 * {@link CreateModelCommand.TypeDefinitionInput} 与
 * {@link org.kitona.zus.service.dto.command.AddTypeDefinitionCommand} 中一致，
 * 本类提供统一的「从该结构构建 TypeDefinition」的方法供创建模型、添加类型定义等用例复用。
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
     * @param relations 关系输入列表
     * @return RelationDefinition 列表
     */
    public static List<RelationDefinition> toRelationDefinitions(List<CreateModelCommand.RelationInput> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        return relations.stream()
                .map(relation -> new RelationDefinition(
                        relation.getRelationName(),
                        relation.getRewriteExpression(),
                        CollectionUtils.isEmpty(relation.getAllowedSubjectTypes())
                                ? Collections.emptySet()
                                : new HashSet<>(relation.getAllowedSubjectTypes())))
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
            Map<String, String> relationsMap = relations.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().rewriteExpression()));
            dto.setRelations(relationsMap);

            Map<String, List<String>> restrictionsMap = relations.entrySet().stream()
                    .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue().restrictions()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new ArrayList<>(entry.getValue().restrictions())
                    ));
            if (!restrictionsMap.isEmpty()) {
                dto.setRelationRestrictions(restrictionsMap);
            }
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

    /**
     * 将关系定义转换为 DTO
     *
     * @param typeDefinitionId 类型定义ID
     * @param relation         关系定义值对象
     * @return 关系结果 DTO
     */
    public static AuthorizationRelationResultDTO toRelationDTO(Long typeDefinitionId, RelationDefinition relation) {
        if (relation == null) {
            return null;
        }

        AuthorizationRelationResultDTO dto = new AuthorizationRelationResultDTO();
        dto.setTypeDefinitionId(typeDefinitionId);
        dto.setRelationName(relation.relationName());
        dto.setRewriteExpression(relation.rewriteExpression());

        Set<String> restrictions = relation.restrictions();
        if (CollectionUtils.isNotEmpty(restrictions)) {
            dto.setAllowedTypes(new ArrayList<>(restrictions));
        }

        return dto;
    }

    /**
     * 批量将关系定义转换为 DTO
     *
     * @param typeDefinitionId 类型定义ID
     * @param relations        关系定义映射
     * @return DTO 列表
     */
    public static List<AuthorizationRelationResultDTO> toRelationDTOList(Long typeDefinitionId,
                                                                         Map<String, RelationDefinition> relations) {
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        return relations.values().stream()
                .map(rel -> toRelationDTO(typeDefinitionId, rel))
                .toList();
    }

}
