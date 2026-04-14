package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ModelRelationPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.SubjectDefinitionPO;
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
    private ISubjectDefinitionPersistenceRepository typeDefinitionPersistenceRepository;

    @Resource
    private IModelRelationPersistenceRepository modelRelationPersistenceRepository;

    @Resource
    private IRelationRestrictionPersistenceRepository relationRestrictionPersistenceRepository;

    @Resource
    private IConditionDefinitionPersistenceRepository conditionDefinitionPersistenceRepository;

    @Override
    public Optional<AuthorizationModelAggregate> findByModelId(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(modelId, storeId)) {
            return Optional.empty();
        }

        Optional<AuthorizationModelPO> repository = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (repository.isEmpty()) {
            return Optional.empty();
        }

        AuthorizationModelAggregate aggregate = AuthorizationModelConverter.toAggregate(repository.get());
        if (aggregate == null) {
            return Optional.empty();
        }

        Map<String, List<TypeDefinition>> map = this.assembleTypeDefinitionEntities(storeId, Set.of(modelId));
        aggregate.reconstituteTypeDefinitions(map.getOrDefault(modelId, Collections.emptyList()));
        aggregate.reconstituteConditionDefinitions(loadConditionDefinitions(storeId, modelId));
        return Optional.of(aggregate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateModel(AuthorizationModelAggregate model) {
        String storeId = model.getStoreId();
        String modelId = model.getModelId();

        Optional<AuthorizationModelPO> existing = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (existing.isEmpty()) {
            authorizationModelPersistenceRepository.createModel(AuthorizationModelConverter.toPO(model));
        } else {
            processRelationDataFromModel(storeId, modelId);
        }

        this.batchSaveTypeDefinitions(storeId, modelId, model.getTypeDefinitions());
        this.batchSaveConditionDefinitions(storeId, modelId, model.getConditionDefinitions());
        return true;
    }

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
        processRelationDataFromModel(storeId, modelId);
        return true;
    }

    private Map<String, List<TypeDefinition>> assembleTypeDefinitionEntities(String storeId, Set<String> modelIds) {
        List<SubjectDefinitionPO> typeDefinitionPOs = typeDefinitionPersistenceRepository.selectByModelIdList(storeId, modelIds);
        if (CollectionUtils.isEmpty(typeDefinitionPOs)) {
            log.info("assembleTypeDefinitionEntities No TypeDefinition found for modelId: {} and storeId: {}", modelIds, storeId);
            return new HashMap<>();
        }

        Set<Long> typeDefIds = typeDefinitionPOs.stream().map(SubjectDefinitionPO::getId).collect(Collectors.toSet());
        List<ModelRelationPO> relationList = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeDefIds);
        if (CollectionUtils.isEmpty(relationList)) {
            log.info("assembleTypeDefinitionEntities No ModelRelation found for typeDefIds: {}", typeDefIds);
            return new HashMap<>();
        }
        Map<Long, List<ModelRelationPO>> relationsByTypeDefId = relationList.stream()
                .collect(Collectors.groupingBy(ModelRelationPO::getTypeDefinitionId));

        Set<Long> relationIds = relationsByTypeDefId.values().stream()
                .flatMap(List::stream)
                .map(ModelRelationPO::getId)
                .collect(Collectors.toSet());
        List<RelationRestrictionPO> relationDefinition = relationRestrictionPersistenceRepository.selectByRelationDefinitionId(relationIds);
        if (CollectionUtils.isEmpty(relationDefinition)) {
            log.info("assembleTypeDefinitionEntities No RelationRestriction found for relationIds: {}", relationIds);
            return new HashMap<>();
        }
        Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId = relationDefinition.stream()
                .collect(Collectors.groupingBy(RelationRestrictionPO::getRelationDefinitionId));

        return typeDefinitionPOs.stream()
                .collect(Collectors.groupingBy(SubjectDefinitionPO::getModelId,
                        Collectors.mapping(
                                typeDefPO -> buildTypeDefinitionEntity(typeDefPO, relationsByTypeDefId, restrictionsByRelationId),
                                Collectors.toList())));
    }

    private TypeDefinition buildTypeDefinitionEntity(SubjectDefinitionPO typeDefPO,
                                                           Map<Long, List<ModelRelationPO>> relationsByTypeDefId,
                                                           Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId) {
        TypeDefinition typeDefEntity = TypeDefinition.reconstitute(
                typeDefPO.getId(),
                typeDefPO.getSubjectType(),
                typeDefPO.getSortOrder()
        );

        List<ModelRelationPO> relationPOs = relationsByTypeDefId.getOrDefault(typeDefPO.getId(), Collections.emptyList());
        relationPOs.stream()
                .map(relationPO -> buildRelationDefinition(relationPO, restrictionsByRelationId))
                .forEach(typeDefEntity::putRelation);

        return typeDefEntity;
    }

    private RelationDefinition buildRelationDefinition(ModelRelationPO relationPO,
                                                       Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId) {
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

    private void processRelationDataFromModel(String storeId, String modelId) {
        conditionDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);

        List<SubjectDefinitionPO> typeDefinitions = typeDefinitionPersistenceRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        typeDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);
        Set<Long> typeIdSet = typeDefinitions.stream().map(SubjectDefinitionPO::getId).collect(Collectors.toSet());
        List<ModelRelationPO> relations = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeIdSet);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        Set<Long> relationIdSet = relations.stream().map(ModelRelationPO::getId).collect(Collectors.toSet());
        modelRelationPersistenceRepository.deleteByTypeDefinitionIds(typeIdSet);
        List<RelationRestrictionPO> restrictions = relationRestrictionPersistenceRepository.selectByRelationDefinitionId(relationIdSet);
        if (CollectionUtils.isEmpty(restrictions)) {
            return;
        }

        relationRestrictionPersistenceRepository.deleteByRelationDefinitionIds(relationIdSet);
    }

    private Map<String, Long> batchSaveSubjectDefinitions(String storeId, String modelId,
                                                          List<TypeDefinition> typeDefinitions) {
        List<SubjectDefinitionPO> poList = new ArrayList<>(typeDefinitions.size());
        for (int i = 0; i < typeDefinitions.size(); i++) {
            TypeDefinition entity = typeDefinitions.get(i);
            SubjectDefinitionPO po = new SubjectDefinitionPO();
            po.setStoreId(storeId);
            po.setModelId(modelId);
            po.setSubjectType(entity.getSubjectType());
            po.setSortOrder(i);
            poList.add(po);
        }

        typeDefinitionPersistenceRepository.saveBatch(poList);

        return poList.stream()
                .collect(Collectors.toMap(SubjectDefinitionPO::getSubjectType, SubjectDefinitionPO::getId));
    }

    private Map<String, Long> batchSaveModelRelations(List<TypeDefinition> typeDefinitions,
                                                      Map<String, Long> typeToIdMap) {
        List<ModelRelationPO> poList = new ArrayList<>();

        for (TypeDefinition typeEntity : typeDefinitions) {
            Map<String, RelationDefinition> relations = typeEntity.getRelations();
            if (relations == null || relations.isEmpty()) {
                continue;
            }

            Long typeDefId = typeToIdMap.get(typeEntity.getSubjectType());
            for (RelationDefinition relationDef : relations.values()) {
                ModelRelationPO po = new ModelRelationPO();
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
                ModelRelationPO::getId));
    }

    private void batchSaveTypeDefinitions(String storeId, String modelId, List<TypeDefinition> typeDefinitions) {
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        Map<String, Long> typeToIdMap = batchSaveSubjectDefinitions(storeId, modelId, typeDefinitions);
        Map<String, Long> relationKeyToIdMap = batchSaveModelRelations(typeDefinitions, typeToIdMap);
        if (relationKeyToIdMap.isEmpty()) {
            return;
        }

        batchSaveRelationRestrictions(typeDefinitions, relationKeyToIdMap);
    }

    private void batchSaveRelationRestrictions(List<TypeDefinition> typeDefinitions,
                                               Map<String, Long> relationKeyToIdMap) {
        List<RelationRestrictionPO> poList = new ArrayList<>();

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
                    poList.add(new RelationRestrictionPO(relationId, allowedType, allowedSubjectRelation));
                }
            }
        }

        if (poList.isEmpty()) {
            return;
        }
        relationRestrictionPersistenceRepository.saveBatch(poList);
    }

    private String toRestrictionValue(RelationRestrictionPO po) {
        if (po == null || StringUtils.isBlank(po.getAllowedType())) {
            return null;
        }
        if (StringUtils.isBlank(po.getAllowedSubjectRelation())) {
            return po.getAllowedType();
        }
        return po.getAllowedType() + "#" + po.getAllowedSubjectRelation();
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

    private List<ConditionDefinition> loadConditionDefinitions(String storeId, String modelId) {
        return conditionDefinitionPersistenceRepository.selectByModelId(storeId, modelId).stream()
                .map(po -> ConditionDefinition.reconstitute(
                        po.getId(),
                        po.getConditionName(),
                        po.getExpression(),
                        po.getParameterSchema(),
                        po.getDescription()))
                .toList();
    }

    private void batchSaveConditionDefinitions(String storeId, String modelId, List<ConditionDefinition> definitions) {
        if (CollectionUtils.isEmpty(definitions)) {
            return;
        }
        List<ConditionDefinitionPO> poList = definitions.stream()
                .map(definition -> {
                    ConditionDefinitionPO po = new ConditionDefinitionPO();
                    po.setId(definition.getId());
                    po.setStoreId(storeId);
                    po.setModelId(modelId);
                    po.setConditionName(definition.getConditionName());
                    po.setExpression(definition.getExpression());
                    po.setParameterSchema(definition.getParameterSchema());
                    po.setDescription(definition.getDescription());
                    return po;
                })
                .toList();
        conditionDefinitionPersistenceRepository.saveBatch(poList);
    }
}
