package org.kitona.zus.service.assembler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.domain.valueobject.TypeDefinition;
import org.kitona.zus.service.dto.response.RelationResultDTO;
import org.kitona.zus.service.dto.response.TypeDefinitionResultDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类型定义 Assembler
 *
 * <p>负责 {@link TypeDefinitionEntity} 与 DTO/Command 之间的双向转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO/Command 之间的转换。
 * <p>类型定义的结构化输入（type + relations + relationRestrictions）在
 * {@link org.kitona.zus.service.dto.command.CreateModelCommand.TypeDefinitionInput} 与
 * {@link org.kitona.zus.service.dto.command.AddTypeDefinitionCommand} 中一致，
 * 本类提供统一的「从该结构构建 TypeDefinitionEntity」的方法供创建模型、添加类型定义等用例复用。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class TypeDefinitionAssembler {

    private TypeDefinitionAssembler() {
    }

    // ========== Command/Input → RelationDefinition 列表（数据转换） ==========

    /**
     * 从「关系定义映射 + 关系限制映射」转换为 RelationDefinition 列表
     *
     * <p>DDD 规范：Assembler 只负责数据转换，不直接创建领域实体。
     * 此方法将 DTO 层的 Map 结构转换为领域层的 RelationDefinition 列表，
     * 由调用方通过 {@link TypeDefinitionEntity#createWithRelations} 工厂方法创建实体。
     *
     * @param relations            关系名到重写表达式的映射
     * @param relationRestrictions 关系名到允许的主体类型列表的映射，可为 null
     * @return RelationDefinition 列表
     */
    public static List<RelationDefinition> toRelationDefinitions(
            Map<String, String> relations,
            Map<String, List<String>> relationRestrictions) {
        if (MapUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }

        Map<String, List<String>> restrictions = relationRestrictions != null ? relationRestrictions : Collections.emptyMap();

        List<RelationDefinition> result = new ArrayList<>();
        relations.forEach((relationName, rewriteExpression) -> {
            Set<String> allowedTypes = restrictions.containsKey(relationName)
                    ? new HashSet<>(restrictions.get(relationName))
                    : Collections.emptySet();
            result.add(new RelationDefinition(relationName, rewriteExpression, allowedTypes));
        });

        return result;
    }

    // ========== Entity → DTO ==========

    /**
     * 将类型定义实体转换为 DTO
     *
     * @param entity 类型定义实体
     * @return 类型定义结果 DTO
     */
    public static TypeDefinitionResultDTO toDTO(TypeDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        TypeDefinitionResultDTO dto = new TypeDefinitionResultDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getSubjectType());

        Map<String, RelationDefinition> relations = entity.getRelations();
        if (MapUtils.isNotEmpty(relations)) {
            Map<String, String> relationsMap = relations.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().rewriteExpression()
                    ));
            dto.setRelations(relationsMap);

            Map<String, List<String>> restrictionsMap = new HashMap<>();
            for (Map.Entry<String, RelationDefinition> entry : relations.entrySet()) {
                Set<String> restrictions = entry.getValue().restrictions();
                if (CollectionUtils.isNotEmpty(restrictions)) {
                    restrictionsMap.put(entry.getKey(), new ArrayList<>(restrictions));
                }
            }
            if (MapUtils.isNotEmpty(restrictionsMap)) {
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
    public static List<TypeDefinitionResultDTO> toDTOList(List<TypeDefinitionEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(TypeDefinitionAssembler::toDTO)
                .toList();
    }

    /**
     * 将关系定义转换为 DTO
     *
     * @param typeDefinitionId 类型定义ID
     * @param relation         关系定义值对象
     * @return 关系结果 DTO
     */
    public static RelationResultDTO toRelationDTO(Long typeDefinitionId, RelationDefinition relation) {
        if (relation == null) {
            return null;
        }

        RelationResultDTO dto = new RelationResultDTO();
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
    public static List<RelationResultDTO> toRelationDTOList(Long typeDefinitionId, Map<String, RelationDefinition> relations) {
        if (MapUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        return relations.values().stream()
                .map(rel -> toRelationDTO(typeDefinitionId, rel))
                .toList();
    }

    // ========== Entity → ValueObject 转换（供领域服务使用） ==========

    /**
     * 将类型定义实体转换为领域值对象
     *
     * @param entity 类型定义实体
     * @return 类型定义值对象
     */
    public static TypeDefinition toValueObject(TypeDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        Map<String, RelationDefinition> entityRelations = entity.getRelations();
        Map<String, RelationDefinition> relations = new HashMap<>();
        Map<String, Set<String>> relationRestrictions = new HashMap<>();

        if (MapUtils.isNotEmpty(entityRelations)) {
            entityRelations.forEach((relationName, relationDef) -> {
                relations.put(relationName, new RelationDefinition(
                        relationDef.relationName(),
                        relationDef.rewriteExpression()
                ));

                Set<String> restrictions = relationDef.restrictions();
                if (CollectionUtils.isNotEmpty(restrictions)) {
                    relationRestrictions.put(relationName, restrictions);
                }
            });
        }

        return new TypeDefinition(entity.getSubjectType(), relations, relationRestrictions);
    }

    /**
     * 批量将类型定义实体转换为领域值对象
     *
     * @param entities 类型定义实体列表
     * @return 值对象列表
     */
    public static List<TypeDefinition> toValueObjectList(List<TypeDefinitionEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(TypeDefinitionAssembler::toValueObject).toList();
    }
}
