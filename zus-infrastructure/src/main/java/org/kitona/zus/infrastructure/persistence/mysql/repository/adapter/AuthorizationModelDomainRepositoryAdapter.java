package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelStructure;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IConditionDefinitionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IModelRelationPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IRelationRestrictionPersistenceRepository;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ISubjectDefinitionPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 授权模型仓储领域接口适配器
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

    @Resource
    private AuthorizationModelStructureSynchronizer structureSynchronizer;

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

        aggregate.reconstituteStructure(loadStructure(storeId, modelId));
        return Optional.of(aggregate);
    }

    /**
     * 创建授权模型，并一次性写入模型结构化子表。
     *
     * @param model 授权模型聚合根
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createModel(AuthorizationModelAggregate model) {
        String storeId = model.getStoreId();
        String modelId = model.getModelId();
        if (authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId).isPresent()) {
            log.warn("创建授权模型失败，模型已存在: storeId={}, modelId={}", storeId, modelId);
            return false;
        }

        authorizationModelPersistenceRepository.createModel(AuthorizationModelConverter.toPO(model));
        structureSynchronizer.insert(storeId, modelId, model.getStructure());
        log.info("创建授权模型完成: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    /**
     * 更新授权模型主表元信息，不重写模型结构化子表。
     *
     * @param model 授权模型聚合根
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateModelMetadata(AuthorizationModelAggregate model) {
        String storeId = model.getStoreId();
        String modelId = model.getModelId();

        Optional<AuthModelPO> existing = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (existing.isEmpty()) {
            log.warn("更新授权模型元信息失败，模型不存在: storeId={}, modelId={}", storeId, modelId);
            return false;
        }

        AuthModelPO updatePO = AuthorizationModelConverter.toPO(model);
        updatePO.setId(existing.get().getId());
        authorizationModelPersistenceRepository.updateModel(updatePO);
        log.info("更新授权模型元信息完成: storeId={}, modelId={}", storeId, modelId);
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
        structureSynchronizer.deleteAll(storeId, modelId);
        return true;
    }

    private AuthorizationModelStructure loadStructure(String storeId, String modelId) {
        return AuthorizationModelStructure.fromTypesAndConditions(
                loadTypeDefinitions(storeId, modelId),
                loadConditionDefinitions(storeId, modelId));
    }

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
            return new HashMap<>();
        }

        Set<Long> typeDefIds = typeDefinitionPOs.stream().map(TypeDefinitionPO::getId).collect(Collectors.toSet());
        List<RelationDefinitionPO> relationList = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeDefIds);

        if (CollectionUtils.isEmpty(relationList)) {
            return typeDefinitionPOs.stream()
                    .collect(Collectors.groupingBy(TypeDefinitionPO::getModelId,
                            Collectors.mapping(
                                    po -> TypeDefinition.reconstitute(po.getId(), po.getSubjectType(), po.getSortOrder()),
                                    Collectors.toList())));
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

    private String toRestrictionValue(TypeRestrictionPO po) {
        if (po == null || StringUtils.isBlank(po.getAllowedType())) {
            return null;
        }
        if (StringUtils.isBlank(po.getAllowedSubjectRelation())) {
            return po.getAllowedType();
        }
        return po.getAllowedType() + "#" + po.getAllowedSubjectRelation();
    }

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
}
