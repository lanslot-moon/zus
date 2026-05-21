package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelStructure;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IConditionDefinitionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IModelRelationPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IRelationRestrictionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ISubjectDefinitionPersistenceRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 授权模型结构写入器。
 *
 * <p>WriteAuthorizationModel 每次都会创建新的 modelId，因此结构化子表只在模型创建时插入一次。
 * 发布、废弃、激活只更新模型主表或 Store 指针，不应重写 type/relation 子结构。
 */
@Component
class AuthorizationModelStructureSynchronizer {

    /**
     * 类型定义持久化仓储。
     */
    @Resource
    private ISubjectDefinitionPersistenceRepository typeRepository;

    /**
     * 关系定义持久化仓储。
     */
    @Resource
    private IModelRelationPersistenceRepository relationRepository;

    /**
     * 类型限制持久化仓储。
     */
    @Resource
    private IRelationRestrictionPersistenceRepository restrictionRepository;

    /**
     * 条件定义持久化仓储。
     */
    @Resource
    private IConditionDefinitionPersistenceRepository conditionRepository;

    /**
     * 插入结构化模型到数据库子表。
     *
     * @param storeId   Store 标识
     * @param modelId   授权模型标识
     * @param structure 结构化模型
     */
    void insert(String storeId, String modelId, AuthorizationModelStructure structure) {
        AuthorizationModelStructure safeStructure = structure == null ? AuthorizationModelStructure.empty() : structure;
        saveConditions(storeId, modelId, safeStructure.conditions());
        saveTypesAndRelations(storeId, modelId, safeStructure.orderedTypes());
    }

    /**
     * 删除模型下所有结构化子表数据。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     */
    void deleteAll(String storeId, String modelId) {
        deleteConditions(storeId, modelId);
        List<TypeDefinitionPO> types = typeRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(types)) {
            return;
        }
        Set<Long> typeIds = types.stream().map(TypeDefinitionPO::getId).collect(Collectors.toSet());
        deleteRelationsAndRestrictions(typeIds);
        typeRepository.removeByIds(typeIds);
    }

    private void saveConditions(String storeId, String modelId, List<ConditionDefinition> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return;
        }
        List<ConditionDefinitionPO> rows = conditions.stream()
                .map(condition -> toConditionPO(storeId, modelId, condition))
                .toList();
        conditionRepository.saveBatch(rows);
    }

    private void saveTypesAndRelations(String storeId, String modelId, List<TypeDefinition> types) {
        if (CollectionUtils.isEmpty(types)) {
            return;
        }
        List<TypeDefinitionPO> typeRows = toTypeRows(storeId, modelId, types);
        typeRepository.saveBatch(typeRows);

        List<RelationDefinitionPO> relationRows = toRelationRows(typeRows, types);
        if (CollectionUtils.isEmpty(relationRows)) {
            return;
        }
        relationRepository.saveBatch(relationRows);
        saveRestrictions(relationRows, types);
    }

    private List<TypeDefinitionPO> toTypeRows(String storeId, String modelId, List<TypeDefinition> types) {
        List<TypeDefinitionPO> rows = new ArrayList<>(types.size());
        for (int sortOrder = 0; sortOrder < types.size(); sortOrder++) {
            TypeDefinition type = types.get(sortOrder);
            TypeDefinitionPO row = new TypeDefinitionPO();
            row.setStoreId(storeId);
            row.setModelId(modelId);
            row.setSubjectType(type.getSubjectType());
            row.setSortOrder(sortOrder);
            rows.add(row);
        }
        return rows;
    }

    private List<RelationDefinitionPO> toRelationRows(List<TypeDefinitionPO> typeRows, List<TypeDefinition> types) {
        List<RelationDefinitionPO> rows = new ArrayList<>();
        for (int index = 0; index < types.size(); index++) {
            TypeDefinition type = types.get(index);
            TypeDefinitionPO typeRow = typeRows.get(index);
            Map<String, RelationDefinition> relations = type.getRelations() == null ? Map.of() : type.getRelations();
            for (RelationDefinition relation : relations.values()) {
                rows.add(toRelationPO(typeRow.getId(), relation));
            }
        }
        return rows;
    }

    private void saveRestrictions(List<RelationDefinitionPO> relationRows, List<TypeDefinition> types) {
        List<TypeRestrictionPO> rows = new ArrayList<>();
        int relationIndex = 0;
        for (TypeDefinition type : types) {
            Map<String, RelationDefinition> relations = type.getRelations() == null ? Map.of() : type.getRelations();
            for (RelationDefinition relation : relations.values()) {
                RelationDefinitionPO relationRow = relationRows.get(relationIndex++);
                rows.addAll(toRestrictionRows(relationRow.getId(), relation.restrictions()));
            }
        }
        if (CollectionUtils.isNotEmpty(rows)) {
            restrictionRepository.saveBatch(rows);
        }
    }

    private RelationDefinitionPO toRelationPO(Long typeDefinitionId, RelationDefinition relation) {
        RelationDefinitionPO row = new RelationDefinitionPO();
        row.setTypeDefinitionId(typeDefinitionId);
        row.setRelationName(relation.relationName());
        row.setRewriteExpression(relation.rewriteExpression());
        return row;
    }

    private List<TypeRestrictionPO> toRestrictionRows(Long relationId, Set<String> restrictions) {
        if (CollectionUtils.isEmpty(restrictions)) {
            return List.of();
        }
        List<TypeRestrictionPO> rows = new ArrayList<>(restrictions.size());
        for (String restriction : restrictions) {
            String allowedType = extractAllowedType(restriction);
            String subjectRelation = extractAllowedSubjectRelation(restriction);
            rows.add(new TypeRestrictionPO(relationId, allowedType, subjectRelation == null ? "" : subjectRelation));
        }
        return rows;
    }

    private ConditionDefinitionPO toConditionPO(String storeId, String modelId, ConditionDefinition condition) {
        ConditionDefinitionPO row = new ConditionDefinitionPO();
        row.setStoreId(storeId);
        row.setModelId(modelId);
        row.setConditionName(condition.getConditionName());
        row.setExpression(condition.getExpression());
        row.setParameterSchema(condition.getParameterSchema());
        row.setDescription(condition.getDescription());
        return row;
    }

    private void deleteConditions(String storeId, String modelId) {
        conditionRepository.remove(new LambdaQueryWrapper<ConditionDefinitionPO>()
                .eq(ConditionDefinitionPO::getStoreId, storeId)
                .eq(ConditionDefinitionPO::getModelId, modelId));
    }

    private void deleteRelationsAndRestrictions(Set<Long> typeIds) {
        List<RelationDefinitionPO> relations = relationRepository.list(new LambdaQueryWrapper<RelationDefinitionPO>()
                .in(RelationDefinitionPO::getTypeDefinitionId, typeIds));
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        Set<Long> relationIds = relations.stream().map(RelationDefinitionPO::getId).collect(Collectors.toSet());
        restrictionRepository.remove(new LambdaQueryWrapper<TypeRestrictionPO>()
                .in(TypeRestrictionPO::getRelationDefinitionId, relationIds));
        relationRepository.removeByIds(relationIds);
    }

    private String extractAllowedType(String restrictionValue) {
        if (StringUtils.isBlank(restrictionValue)) {
            return restrictionValue;
        }
        int relationIndex = restrictionValue.indexOf('#');
        if (relationIndex < 0) {
            return restrictionValue;
        }
        return restrictionValue.substring(0, relationIndex);
    }

    private String extractAllowedSubjectRelation(String restrictionValue) {
        if (StringUtils.isBlank(restrictionValue)) {
            return null;
        }
        int relationIndex = restrictionValue.indexOf('#');
        if (relationIndex < 0 || relationIndex == restrictionValue.length() - 1) {
            return null;
        }
        return restrictionValue.substring(relationIndex + 1);
    }
}
