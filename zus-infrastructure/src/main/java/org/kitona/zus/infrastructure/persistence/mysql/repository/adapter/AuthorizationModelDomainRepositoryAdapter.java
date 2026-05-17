package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IConditionDefinitionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IModelRelationPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IRelationRestrictionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ISubjectDefinitionPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 授权模型仓储领域接口适配器
 *
 * <p>实现 domain 层 {@link IAuthorizationModelDomainRepository}，
 * 委托基础设施层持久化契约并完成 PO 到聚合根的转换。
 */
@Slf4j
@Repository
public class AuthorizationModelDomainRepositoryAdapter implements IAuthorizationModelDomainRepository {

    @Resource
    private IAuthorizationModelPersistenceRepository authorizationModelPersistenceRepository;

    @Resource
    private ISubjectDefinitionPersistenceRepository subjectDefinitionPersistenceRepository;

    @Resource
    private IModelRelationPersistenceRepository modelRelationPersistenceRepository;

    @Resource
    private IRelationRestrictionPersistenceRepository relationRestrictionPersistenceRepository;

    @Resource
    private IConditionDefinitionPersistenceRepository conditionDefinitionPersistenceRepository;


    /**
     * 根据门店ID和模型ID查找授权模型聚合根
     *
     * @param storeId 门店ID
     * @param modelId 模型ID
     * @return 授权模型聚合根的Optional对象，如果不存在则返回空
     */
    @Override
    public Optional<AuthorizationModelAggregate> findByModelId(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(modelId, storeId)) {
            return Optional.empty();
        }

        Optional<AuthModelPO> repository = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (repository.isEmpty()) {
            return Optional.empty();
        }

        AuthorizationModelAggregate aggregate = AuthorizationModelConverter.toAggregate(repository.get());
        if (aggregate == null) {
            return Optional.empty();
        }

        aggregate.reconstituteTypeDefinitions(loadTypeDefinitions(storeId, modelId));
        aggregate.reconstituteConditionDefinitions(loadConditionDefinitions(storeId, modelId));
        return Optional.of(aggregate);
    }

    /**
     * 保存或更新授权模型
     *
     * @param model 授权模型聚合根
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateModel(AuthorizationModelAggregate model) {
        log.info("saveOrUpdateModel 参数为: {}", JacksonUtil.toJSONString(model));
        String storeId = model.getStoreId();
        String modelId = model.getModelId();

        Optional<AuthModelPO> existing = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (existing.isEmpty()) {
            authorizationModelPersistenceRepository.createModel(AuthorizationModelConverter.toPO(model));
        } else {
            // 已存在：更新模型本身（status / description / dslText 等）并清理关联结构后重建
            AuthModelPO updatePO = AuthorizationModelConverter.toPO(model);
            updatePO.setId(existing.get().getId());
            authorizationModelPersistenceRepository.updateModel(updatePO);
            this.deleteModelAssociateStructure(storeId, modelId);
            log.info("saveOrUpdateModel 模型关联结构已删除,storeId:{}, modelId:{}", storeId, modelId);
        }

        saveTypeDefinitions(storeId, modelId, model.getTypeDefinitions());
        saveConditionDefinitions(storeId, modelId, model.getConditionDefinitions());
        return true;
    }

    /**
     * 删除草稿状态的授权模型
     *
     * @param storeId 门店ID
     * @param modelId 模型ID
     * @return 操作是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteDraftModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }

        boolean deleted = authorizationModelPersistenceRepository.deleteDraftModel(storeId, modelId);
        if (!deleted) {
            return false;
        }
        this.deleteModelAssociateStructure(storeId, modelId);
        return true;
    }

    /**
     * 加载指定模型的类型定义列表
     *
     * @param storeId 门店ID
     * @param modelId 模型ID
     * @return 类型定义列表
     */
    private List<TypeDefinition> loadTypeDefinitions(String storeId, String modelId) {
        Map<String, List<TypeDefinition>> typeDefinitionsByModelId = assembleTypeDefinitions(storeId, Set.of(modelId));
        return typeDefinitionsByModelId.getOrDefault(modelId, Collections.emptyList());
    }

    /**
     * 组装多个模型的类型定义及其关联关系
     *
     * @param storeId  门店ID
     * @param modelIds 模型ID集合
     * @return 按模型ID分组的类型定义Map
     */
    private Map<String, List<TypeDefinition>> assembleTypeDefinitions(String storeId, Set<String> modelIds) {
        List<TypeDefinitionPO> typeDefinitionPOs = subjectDefinitionPersistenceRepository.selectByModelIdList(storeId, modelIds);
        if (CollectionUtils.isEmpty(typeDefinitionPOs)) {
            log.info("assembleTypeDefinitions No type definition found for modelIds: {} and storeId: {}", modelIds, storeId);
            return new HashMap<>();
        }

        Set<Long> typeDefIds = typeDefinitionPOs.stream().map(TypeDefinitionPO::getId).collect(Collectors.toSet());
        List<RelationDefinitionPO> relationList = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeDefIds);
        if (CollectionUtils.isEmpty(relationList)) {
            log.info("assembleTypeDefinitions No relation found for typeDefinitionIds: {}", typeDefIds);
            return new HashMap<>();
        }
        Map<Long, List<RelationDefinitionPO>> relationsByTypeDefId = relationList.stream()
                .collect(Collectors.groupingBy(RelationDefinitionPO::getTypeDefinitionId));

        Set<Long> relationIds = relationsByTypeDefId.values().stream()
                .flatMap(List::stream)
                .map(RelationDefinitionPO::getId)
                .collect(Collectors.toSet());
        Map<Long, List<TypeRestrictionPO>> restrictionsByRelationId = loadRestrictionsByRelationId(relationIds);

        return typeDefinitionPOs.stream()
                .collect(Collectors.groupingBy(TypeDefinitionPO::getModelId,
                        Collectors.mapping(
                                typeDefPO -> buildTypeDefinitionEntity(typeDefPO, relationsByTypeDefId, restrictionsByRelationId),
                                Collectors.toList())));
    }

    /**
     * 根据关系ID集合加载关系限制
     *
     * @param relationIds 关系ID集合
     * @return 按关系ID分组的关系限制Map
     */
    private Map<Long, List<TypeRestrictionPO>> loadRestrictionsByRelationId(Set<Long> relationIds) {
        if (CollectionUtils.isEmpty(relationIds)) {
            return Collections.emptyMap();
        }
        List<TypeRestrictionPO> restrictions = relationRestrictionPersistenceRepository.selectByRelationDefinitionId(relationIds);
        if (CollectionUtils.isEmpty(restrictions)) {
            return Collections.emptyMap();
        }
        return restrictions.stream().collect(Collectors.groupingBy(TypeRestrictionPO::getRelationDefinitionId));
    }

    /**
     * 构建类型定义实体
     *
     * @param typeDefPO                类型定义PO对象
     * @param relationsByTypeDefId     按类型定义ID分组的关系Map
     * @param restrictionsByRelationId 按关系ID分组的关系限制Map
     * @return 类型定义实体
     */
    private TypeDefinition buildTypeDefinitionEntity(TypeDefinitionPO typeDefPO,
                                                     Map<Long, List<RelationDefinitionPO>> relationsByTypeDefId,
                                                     Map<Long, List<TypeRestrictionPO>> restrictionsByRelationId) {
        TypeDefinition typeDefEntity = TypeDefinition.reconstitute(
                typeDefPO.getId(),
                typeDefPO.getSubjectType(),
                typeDefPO.getSortOrder()
        );

        List<RelationDefinitionPO> relationPOs = relationsByTypeDefId.getOrDefault(typeDefPO.getId(), Collections.emptyList());
        relationPOs.stream()
                .map(relationPO -> buildRelationDefinition(relationPO, restrictionsByRelationId))
                .forEach(typeDefEntity::putRelation);

        return typeDefEntity;
    }

    /**
     * 构建关系定义实体
     *
     * @param relationPO               关系PO对象
     * @param restrictionsByRelationId 按关系ID分组的关系限制Map
     * @return 关系定义实体
     */
    private RelationDefinition buildRelationDefinition(RelationDefinitionPO relationPO,
                                                       Map<Long, List<TypeRestrictionPO>> restrictionsByRelationId) {
        Set<String> restrictions = restrictionsByRelationId.getOrDefault(relationPO.getId(), Collections.emptyList())
                .stream()
                .map(this::toRestrictionValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return new RelationDefinition(
                relationPO.getRelationName(),
                relationPO.getRewriteExpression(),
                restrictions
        );
    }

    /**
     * 删除模型关联结构（包括类型定义、关系定义和关系限制,不删除Model本身）
     *
     * @param storeId 门店ID
     * @param modelId 模型ID
     */
    private void deleteModelAssociateStructure(String storeId, String modelId) {
        conditionDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);

        List<TypeDefinitionPO> typeDefinitions = subjectDefinitionPersistenceRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        subjectDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);
        Set<Long> typeIdSet = typeDefinitions.stream().map(TypeDefinitionPO::getId).collect(Collectors.toSet());
        List<RelationDefinitionPO> relations = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeIdSet);
        modelRelationPersistenceRepository.deleteByTypeDefinitionIds(typeIdSet);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        Set<Long> relationIdSet = relations.stream().map(RelationDefinitionPO::getId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(relationIdSet)) {
            return;
        }
        relationRestrictionPersistenceRepository.deleteByRelationDefinitionIds(relationIdSet);
    }

    /**
     * 保存主体定义
     *
     * @param storeId         门店ID
     * @param modelId         模型ID
     * @param typeDefinitions 类型定义列表
     * @return 按主体类型分组的ID映射Map
     */
    private Map<String, Long> saveSubjectDefinitions(String storeId, String modelId, List<TypeDefinition> typeDefinitions) {
        List<TypeDefinitionPO> poList = new ArrayList<>(typeDefinitions.size());
        for (int i = 0; i < typeDefinitions.size(); i++) {
            TypeDefinition entity = typeDefinitions.get(i);
            TypeDefinitionPO po = new TypeDefinitionPO();
            po.setStoreId(storeId);
            po.setModelId(modelId);
            po.setSubjectType(entity.getSubjectType());
            po.setSortOrder(i);
            poList.add(po);
        }

        subjectDefinitionPersistenceRepository.saveBatch(poList);
        return poList.stream()
                .collect(Collectors.toMap(TypeDefinitionPO::getSubjectType, TypeDefinitionPO::getId));
    }

    /**
     * 保存模型关系
     *
     * @param typeDefinitions 类型定义列表
     * @param typeToIdMap     按主体类型分组的ID映射Map
     * @return 按主体类型:关系名称分组的ID映射Map
     */
    private Map<String, Long> saveModelRelations(List<TypeDefinition> typeDefinitions, Map<String, Long> typeToIdMap) {
        List<RelationDefinitionPO> poList = new ArrayList<>();

        for (TypeDefinition typeEntity : typeDefinitions) {
            Map<String, RelationDefinition> relations = typeEntity.getRelations();
            if (relations == null || relations.isEmpty()) {
                continue;
            }

            Long typeDefId = typeToIdMap.get(typeEntity.getSubjectType());
            for (RelationDefinition relationDef : relations.values()) {
                RelationDefinitionPO po = new RelationDefinitionPO();
                po.setTypeDefinitionId(typeDefId);
                po.setSubjectType(typeEntity.getSubjectType());
                po.setRelationName(relationDef.relationName());
                po.setRewriteExpression(relationDef.rewriteExpression());
                poList.add(po);
            }
        }

        if (poList.isEmpty()) {
            return Collections.emptyMap();
        }

        modelRelationPersistenceRepository.saveBatch(poList);

        return poList.stream().collect(Collectors.toMap(
                po -> po.getSubjectType() + ":" + po.getRelationName(),
                RelationDefinitionPO::getId));
    }

    /**
     * 保存类型定义及其关联关系
     *
     * @param storeId         门店ID
     * @param modelId         模型ID
     * @param typeDefinitions 类型定义列表
     */
    private void saveTypeDefinitions(String storeId, String modelId, List<TypeDefinition> typeDefinitions) {
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        Map<String, Long> typeToIdMap = saveSubjectDefinitions(storeId, modelId, typeDefinitions);
        Map<String, Long> relationKeyToIdMap = saveModelRelations(typeDefinitions, typeToIdMap);
        if (relationKeyToIdMap.isEmpty()) {
            return;
        }

        saveRelationRestrictions(typeDefinitions, relationKeyToIdMap);
    }

    /**
     * 保存关系限制
     *
     * @param typeDefinitions    类型定义列表
     * @param relationKeyToIdMap 按主体类型:关系名称分组的ID映射Map
     */
    private void saveRelationRestrictions(List<TypeDefinition> typeDefinitions, Map<String, Long> relationKeyToIdMap) {
        List<TypeRestrictionPO> poList = new ArrayList<>();

        for (TypeDefinition typeEntity : typeDefinitions) {
            Map<String, RelationDefinition> relations = typeEntity.getRelations();
            if (relations == null || relations.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, RelationDefinition> entry : relations.entrySet()) {
                String relationName = entry.getKey();
                RelationDefinition relationDef = entry.getValue();
                Set<String> restrictions = relationDef.restrictions();

                if (CollectionUtils.isEmpty(restrictions)) {
                    continue;
                }

                String relationKey = typeEntity.getSubjectType() + ":" + relationName;
                Long relationId = relationKeyToIdMap.get(relationKey);

                for (String restrictionValue : restrictions) {
                    String allowedType = extractAllowedType(restrictionValue);
                    String allowedSubjectRelation = extractAllowedSubjectRelation(restrictionValue);
                    poList.add(new TypeRestrictionPO(relationId, allowedType, allowedSubjectRelation));
                }
            }
        }

        if (poList.isEmpty()) {
            return;
        }
        relationRestrictionPersistenceRepository.saveBatch(poList);
    }

    /**
     * 将关系限制PO转换为限制值字符串
     *
     * @param po 关系限制PO对象
     * @return 限制值字符串，格式为"allowedType"或"allowedType#allowedSubjectRelation"
     */
    private String toRestrictionValue(TypeRestrictionPO po) {
        if (po == null || StringUtils.isBlank(po.getAllowedType())) {
            return null;
        }
        if (StringUtils.isBlank(po.getAllowedSubjectRelation())) {
            return po.getAllowedType();
        }
        return po.getAllowedType() + "#" + po.getAllowedSubjectRelation();
    }

    /**
     * 从限制值字符串中提取允许的类型
     *
     * @param restrictionValue 限制值字符串
     * @return 允许的类型
     */
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

    /**
     * 从限制值字符串中提取允许的主体关系
     *
     * @param restrictionValue 限制值字符串
     * @return 允许的主体关系，如果不存在则返回null
     */
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

    /**
     * 加载条件定义列表
     *
     * @param storeId 门店ID
     * @param modelId 模型ID
     * @return 条件定义列表
     */
    private List<ConditionDefinition> loadConditionDefinitions(String storeId, String modelId) {
        List<ConditionDefinitionPO> list = conditionDefinitionPersistenceRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(po -> ConditionDefinition.reconstitute(
                        po.getId(),
                        po.getConditionName(),
                        po.getExpression(),
                        po.getParameterSchema(),
                        po.getDescription()))
                .toList();
    }

    /**
     * 保存条件定义列表
     *
     * @param storeId     门店ID
     * @param modelId     模型ID
     * @param definitions 条件定义列表
     */
    private void saveConditionDefinitions(String storeId, String modelId, List<ConditionDefinition> definitions) {
        if (CollectionUtils.isEmpty(definitions)) {
            return;
        }
        List<ConditionDefinitionPO> poList = definitions.stream()
                .map(definition -> MapstructUtil.convert(definition, ConditionDefinitionPO.class))
                .toList();

        poList.forEach(item -> item.setModelId(modelId).setStoreId(storeId));
        conditionDefinitionPersistenceRepository.saveBatch(poList);
    }
}
