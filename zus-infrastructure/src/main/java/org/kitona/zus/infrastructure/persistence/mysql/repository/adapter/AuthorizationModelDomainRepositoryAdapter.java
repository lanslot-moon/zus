package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
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

import java.util.ArrayList;
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
 *
 * 该类实现了IAuthorizationModelDomainRepository接口，作为领域仓储和持久化层之间的适配器。
 * 它负责将领域对象与持久化对象进行转换，并实现领域模型的持久化操作。
 */
@Slf4j
@Repository
public class AuthorizationModelDomainRepositoryAdapter implements IAuthorizationModelDomainRepository {

    // 注入持久化层仓储接口
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
     * 根据ID查找授权模型聚合
     *
     * @param id 授权模型ID
     * @return 包含授权模型聚合的Optional，如果不存在则为空Optional
     */
    @Override
    public Optional<AuthorizationModelAggregate> findById(AuthorizationModelId id) {
        // 参数校验
        if (id == null || StringUtils.isAnyBlank(id.modelId(), id.storeId())) {
            return Optional.empty();
        }
        String storeId = id.storeId();
        String modelId = id.modelId();

        // 从持久化层查询模型
        Optional<AuthModelPO> repository = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (repository.isEmpty()) {
            return Optional.empty();
        }

        // 获取到聚合体的header信息
        AuthorizationModelAggregate aggregate = AuthorizationModelConverter.toAggregate(repository.get());
        if (aggregate == null) {
            return Optional.empty();
        }

        // 获取到聚合体的结构信息
        AuthorizationModelStructure structure = AuthorizationModelStructure.fromTypesAndConditions(loadTypeDefinitions(storeId, modelId), loadConditionDefinitions(storeId, modelId));
        aggregate.reconstituteStructure(structure);
        return Optional.of(aggregate);
    }

    /**
     * 保存授权模型聚合根。
     *
     * <p>该方法实现领域仓储的“保存聚合当前状态”语义。它不是单纯新增方法，底层会根据
     * {@code storeId + modelId} 是否已存在决定插入或更新。
     *
     * <p>授权模型结构按版本化不可变方式持久化：首次保存某个 {@code modelId} 时，会写入
     * fga_auth_model 主表以及 type/relation/restriction/condition 等结构化子表；如果同一个
     * {@code modelId} 已存在，则只更新主表生命周期状态、发布时间、DSL 快照等元数据，
     * 不重写结构化子表。需要修改模型结构时，应由应用层创建新的 modelId 后再保存。
     *
     * <p>这个约定用于对齐 OpenFGA 风格的 WriteAuthorizationModel：一次写入产生一个模型版本，
     * 后续 publish、abandon 等生命周期变化只改变该版本的元数据，避免同一 modelId 下结构漂移。
     *
     * @param model 授权模型聚合根
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(AuthorizationModelAggregate model) {
        String storeId = model.getStoreId();
        String modelId = model.getModelId();
        Optional<AuthModelPO> existing = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (existing.isPresent()) {
            AuthModelPO updatePO = AuthorizationModelConverter.toPO(model);
            updatePO.setId(existing.get().getId());
            authorizationModelPersistenceRepository.updateModel(updatePO);
            log.info("保存授权模型完成: storeId={}, modelId={}", storeId, modelId);
            return;
        }

        authorizationModelPersistenceRepository.createModel(AuthorizationModelConverter.toPO(model));
        AuthorizationModelStructure safeStructure = Optional.ofNullable(model.getStructure()).orElse(AuthorizationModelStructure.empty());
        saveConditions(storeId, modelId, safeStructure.conditions());
        saveTypesAndRelations(storeId, modelId, safeStructure.orderedTypes());
        log.info("创建授权模型完成: storeId={}, modelId={}", storeId, modelId);
    }

    /**
     * 移除授权模型聚合根。
     *
     * @param model 授权模型聚合根
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(AuthorizationModelAggregate model) {
        if (model == null || StringUtils.isAnyBlank(model.getStoreId(), model.getModelId())) {
            return;
        }
        String storeId = model.getStoreId();
        String modelId = model.getModelId();
        // 1.删除模型信息
        boolean deleted = authorizationModelPersistenceRepository.deleteDraftModel(storeId, modelId);
        if (!deleted) {
            return;
        }

        // 2.删除模型附带的ABAC拓展条件信息
        conditionDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);
        List<TypeDefinitionPO> types = subjectDefinitionPersistenceRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(types)) {
            return;
        }

        Set<Long> typeIds = types.stream().map(TypeDefinitionPO::getId).collect(Collectors.toSet());
        List<RelationDefinitionPO> relations = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeIds);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }
        // 3.删除模型关系定义信息
        Set<Long> relationIds = relations.stream().map(RelationDefinitionPO::getId).collect(Collectors.toSet());
        modelRelationPersistenceRepository.removeByIds(relationIds);
        // 4.删除模型关系定义的约束信息
        relationRestrictionPersistenceRepository.deleteByRelationDefinitionIds(relationIds);
        // 5.删除类型定义信息
        subjectDefinitionPersistenceRepository.removeByIds(typeIds);
    }


    private void saveConditions(String storeId, String modelId, List<ConditionDefinition> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return;
        }
        List<ConditionDefinitionPO> rows = conditions.stream()
                .map(condition -> toConditionPO(storeId, modelId, condition))
                .toList();
        conditionDefinitionPersistenceRepository.saveBatch(rows);
    }

    private void saveTypesAndRelations(String storeId, String modelId, List<TypeDefinition> types) {
        if (CollectionUtils.isEmpty(types)) {
            return;
        }
        List<TypeDefinitionPO> typeRows = toTypeRows(storeId, modelId, types);
        if (CollectionUtils.isEmpty(typeRows)) {
            return;
        }
        subjectDefinitionPersistenceRepository.saveBatch(typeRows);

        List<RelationDefinitionPO> relationRows = toRelationRows(typeRows, types);
        if (CollectionUtils.isEmpty(relationRows)) {
            return;
        }
        modelRelationPersistenceRepository.saveBatch(relationRows);
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
            relationRestrictionPersistenceRepository.saveBatch(rows);
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
            rows.add(new TypeRestrictionPO(relationId, extractAllowedType(restriction),
                    StringUtils.defaultString(extractAllowedSubjectRelation(restriction))));
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
