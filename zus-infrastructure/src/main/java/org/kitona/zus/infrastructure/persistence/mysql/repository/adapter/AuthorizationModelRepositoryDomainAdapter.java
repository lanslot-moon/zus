package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.valueobject.CursorPageResult;
import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ModelRelationPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.SubjectDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
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
 * 实现 domain 层 IAuthorizationModelRepository，委托基础设施层仓储并做 PO↔聚合根 转换。
 * 按照 DDD 规范，Repository 操作聚合根 {@link AuthorizationModelAggregate}。
 *
 * @author kitona
 */
@Slf4j
@Repository
public class AuthorizationModelRepositoryDomainAdapter implements IAuthorizationModelDomainRepository {

    @Resource
    private IAuthorizationModelPersistenceRepository authorizationModelPersistenceRepository;

    @Resource
    private ISubjectDefinitionPersistenceRepository typeDefinitionPersistenceRepository;

    @Resource
    private IModelRelationPersistenceRepository modelRelationPersistenceRepository;

    @Resource
    private IRelationRestrictionPersistenceRepository relationRestrictionPersistenceRepository;

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

        Map<String, List<TypeDefinitionEntity>> map = this.assembleTypeDefinitionEntities(storeId, Set.of(modelId));
        aggregate.reconstituteTypeDefinitions(map.getOrDefault(modelId, Collections.emptyList()));
        return Optional.of(aggregate);
    }

    @Override
    public List<AuthorizationModelAggregate> findByStoreId(String storeId) {
        if (StringUtils.isAnyBlank(storeId)) {
            return Collections.emptyList();
        }

        List<AuthorizationModelPO> modelPOS = authorizationModelPersistenceRepository.findByStoreId(storeId);
        if (CollectionUtils.isEmpty(modelPOS)) {
            return Collections.emptyList();
        }

        Set<String> modelSetId = modelPOS.stream().map(AuthorizationModelPO::getModelId).collect(Collectors.toSet());
        List<AuthorizationModelAggregate> aggregates = modelPOS.stream().map(AuthorizationModelConverter::toAggregate).toList();
        if (CollectionUtils.isEmpty(aggregates)) {
            return Collections.emptyList();
        }
        Map<String, List<TypeDefinitionEntity>> assembled = this.assembleTypeDefinitionEntities(storeId, modelSetId);
        aggregates.forEach(aggregate -> aggregate.reconstituteTypeDefinitions(assembled.getOrDefault(aggregate.getModelId(), Collections.emptyList())));
        return aggregates;
    }

    @Override
    public List<AuthorizationModelAggregate> findByStoreIdAndStatus(String storeId, ModelPublishStatus status) {
        if (StringUtils.isAnyBlank(storeId) || status == null) {
            return Collections.emptyList();
        }

        List<AuthorizationModelPO> models = authorizationModelPersistenceRepository.findByStoreIdAndStatus(storeId, status.getStatus());
        if (CollectionUtils.isEmpty(models)) {
            log.info("findByStoreIdAndStatus 不存在对应模型 storeId: {} and status: {}", storeId, status);
            return Collections.emptyList();
        }

        Set<String> modelSetId = models.stream().map(AuthorizationModelPO::getModelId).collect(Collectors.toSet());
        List<AuthorizationModelAggregate> aggregates = models.stream().map(AuthorizationModelConverter::toAggregate).toList();
        if (CollectionUtils.isEmpty(aggregates)) {
            log.info("findByStoreIdAndStatus 转换失败 storeId: {} and status: {}", storeId, status);
            return Collections.emptyList();
        }
        Map<String, List<TypeDefinitionEntity>> assembled = this.assembleTypeDefinitionEntities(storeId, modelSetId);
        aggregates.forEach(aggregate -> aggregate.reconstituteTypeDefinitions(assembled.getOrDefault(aggregate.getModelId(), Collections.emptyList())));
        return aggregates;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateModel(AuthorizationModelAggregate model) {
        String storeId = model.getStoreId();
        String modelId = model.getModelId();

        // 判断是新增还是更新
        Optional<AuthorizationModelPO> existing = authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId);
        if (existing.isEmpty()) {
            authorizationModelPersistenceRepository.createModel(AuthorizationModelConverter.toPO(model));
        } else {
            // 更新时先删除旧的子实体
            processRelationDataFromModel(storeId, modelId);
        }

        // 级联批量保存类型定义
        this.batchSaveTypeDefinitions(storeId, modelId, model.getTypeDefinitions());
        return true;
    }


    @Override
    public Optional<AuthorizationModelAggregate> findLatestByStoreId(String storeId) {
        Optional<AuthorizationModelPO> latestPO = authorizationModelPersistenceRepository.findLatestByStoreId(storeId);
        if (latestPO.isEmpty()) {
            return Optional.empty();
        }
        AuthorizationModelPO po = latestPO.get();
        AuthorizationModelAggregate aggregate = AuthorizationModelConverter.toAggregate(po);
        if (aggregate == null) {
            return Optional.empty();
        }
        // 加载完整聚合（包含 TypeDefinition）
        Map<String, List<TypeDefinitionEntity>> map = this.assembleTypeDefinitionEntities(po.getStoreId(), Set.of(po.getModelId()));
        aggregate.reconstituteTypeDefinitions(map.getOrDefault(po.getModelId(), Collections.emptyList()));
        return Optional.of(aggregate);
    }

    @Override
    public boolean publishModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        Optional<AuthorizationModelAggregate> modelAggregate = findByModelId(storeId, modelId);
        if (modelAggregate.isEmpty()) {
            log.info("publishModel 不存在对应模型 storeId: {} and modelId: {}", storeId, modelId);
            return false;
        }
        AuthorizationModelAggregate model = modelAggregate.get();
        // 通过聚合根的领域方法发布（会校验状态）
        model.publish();
        AuthorizationModelPO modelPO = AuthorizationModelConverter.toPO(model);
        return authorizationModelPersistenceRepository.updateModel(modelPO);
    }

    @Override
    public boolean deprecateModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        Optional<AuthorizationModelAggregate> modelAggregate = findByModelId(storeId, modelId);
        if (modelAggregate.isEmpty()) {
            log.info("deprecateModel 不存在对应模型 storeId: {} and modelId: {}", storeId, modelId);
            return false;
        }
        AuthorizationModelAggregate model = modelAggregate.get();
        // 通过聚合根的领域方法废弃（会校验状态）
        model.deprecate();
        AuthorizationModelPO modelPO = AuthorizationModelConverter.toPO(model);
        return authorizationModelPersistenceRepository.updateModel(modelPO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }

        // 1.删除对应模型
        authorizationModelPersistenceRepository.deleteModel(storeId, modelId);

        // 2.删除聚合内的所有子实体
        processRelationDataFromModel(storeId, modelId);
        return true;
    }

    @Override
    public long countPublishedModels(String storeId) {
        if (StringUtils.isAnyBlank(storeId)) {
            return 0L;
        }
        return authorizationModelPersistenceRepository.countPublishedModels(storeId);
    }

    @Override
    public boolean existsByModelId(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        return authorizationModelPersistenceRepository.findByModelIdAndStoreId(storeId, modelId).isPresent();
    }

    @Override
    public CursorPageResult<AuthorizationModelAggregate> findPageByCursor(String storeId, Integer status, String pageToken, int pageSize) {
        if (StringUtils.isBlank(storeId)) {
            return CursorPageResult.empty();
        }

        // 轻量级分页查询，不加载类型定义（按接口注释）
        List<AuthorizationModelPO> modelPOs = authorizationModelPersistenceRepository.findPageByCursor(storeId, status, pageToken, pageSize);
        if (CollectionUtils.isEmpty(modelPOs)) {
            return CursorPageResult.empty();
        }

        // 转换为聚合根列表
        List<AuthorizationModelAggregate> aggregates = modelPOs.stream()
                .map(AuthorizationModelConverter::toAggregate)
                .toList();

        Set<String> modelIdSet = modelPOs.stream().map(AuthorizationModelPO::getModelId).collect(Collectors.toSet());
        Map<String, List<TypeDefinitionEntity>> map = this.assembleTypeDefinitionEntities(storeId, modelIdSet);
        aggregates.forEach(aggregate -> aggregate.reconstituteTypeDefinitions(map.get(aggregate.getModelId())));


        // 获取最后一条记录的 modelId 作为下一页游标
        String lastModelId = modelPOs.get(modelPOs.size() - 1).getModelId();

        // 使用 CursorPageResult 封装结果，自动判断是否有更多数据
        return CursorPageResult.of(aggregates, pageSize, lastModelId);
    }






    // ==================== 私有方法 ====================

    /**
     * 组装 TypeDefinitionEntity 列表（包含 RelationDefinition 值对象）
     * 该方法将持久化对象(PO)转换为领域实体(Entity)，并建立它们之间的关系
     *
     * @return 组装好的类型定义实体列表，每个实体包含其关系定义
     */
    private Map<String, List<TypeDefinitionEntity>> assembleTypeDefinitionEntities(String storeId, Set<String> modelIds) {
        List<SubjectDefinitionPO> typeDefinitionPOs = typeDefinitionPersistenceRepository.selectByModelIdList(storeId, modelIds);
        if (typeDefinitionPOs == null || typeDefinitionPOs.isEmpty()) {
            log.info("assembleTypeDefinitionEntities No TypeDefinition found for modelId: {} and storeId: {}", modelIds, storeId);
            return new HashMap<>();
        }

        // 3. 批量查询所有 ModelRelation（优化：一次性查询，避免 N+1）
        Set<Long> typeDefIds = typeDefinitionPOs.stream().map(SubjectDefinitionPO::getId).collect(Collectors.toSet());
        List<ModelRelationPO> relationList = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeDefIds);
        if (CollectionUtils.isEmpty(relationList)) {
            log.info("assembleTypeDefinitionEntities No ModelRelation found for typeDefIds: {}", typeDefIds);
            return new HashMap<>();
        }
        Map<Long, List<ModelRelationPO>> relationsByTypeDefId = relationList.stream().collect(Collectors.groupingBy(ModelRelationPO::getTypeDefinitionId));

        // 4. 批量查询所有 RelationRestriction（优化：一次性查询，避免 N+1）
        Set<Long> relationIds = relationsByTypeDefId.values().stream().flatMap(List::stream).map(ModelRelationPO::getId)
                .collect(Collectors.toSet());
        List<RelationRestrictionPO> relationDefinition = relationRestrictionPersistenceRepository.selectByRelationDefinitionId(relationIds);
        if (CollectionUtils.isEmpty(relationDefinition)) {
            log.info("assembleTypeDefinitionEntities No RelationRestriction found for relationIds: {}", relationIds);
            return new HashMap<>();
        }
        Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId = relationDefinition.stream().collect(Collectors.groupingBy(RelationRestrictionPO::getRelationDefinitionId));

        // 按模型ID分组并转换
        return typeDefinitionPOs.stream()
                .collect(Collectors.groupingBy(SubjectDefinitionPO::getModelId,
                        Collectors.mapping(
                                typeDefPO -> buildTypeDefinitionEntity(typeDefPO, relationsByTypeDefId, restrictionsByRelationId),
                                Collectors.toList())));

    }

    /**
     * 构建类型定义实体对象
     *
     * @param typeDefPO 类型定义持久化对象
     * @param relationsByTypeDefId 按类型定义ID分组的模型关系映射
     * @param restrictionsByRelationId 按关系ID分组的限制条件映射
     * @return 构建完成的类型定义实体对象
     */
    private TypeDefinitionEntity buildTypeDefinitionEntity(SubjectDefinitionPO typeDefPO,
                                                           Map<Long, List<ModelRelationPO>> relationsByTypeDefId,
                                                           Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId) {

        // 从持久化数据重建类型定义实体（使用 DDD 规范的 reconstitute 方法）
        TypeDefinitionEntity typeDefEntity = TypeDefinitionEntity.reconstitute(
                typeDefPO.getId(),
                typeDefPO.getSubjectType(),
                typeDefPO.getSortOrder()
        );

        // 获取并处理该类型下的所有关系定义
        List<ModelRelationPO> relationPOs = relationsByTypeDefId.getOrDefault(typeDefPO.getId(), Collections.emptyList());
        // 将关系持久化对象流转换为关系定义对象，并添加到类型定义实体中
        relationPOs.stream()
                .map(relationPO -> buildRelationDefinition(relationPO, restrictionsByRelationId))  // 构建关系定义
                .forEach(typeDefEntity::putRelation);

        return typeDefEntity;
    }

    /**
     * 构建关系定义对象
     * @param relationPO 关系持久化对象，包含关系的基本信息
     * @param restrictionsByRelationId 按关系ID分组的限制条件映射表
     * @return 返回构建好的RelationDefinition对象，包含关系名称、重写表达式和限制条件集合
     */
    private RelationDefinition buildRelationDefinition(ModelRelationPO relationPO,
                                                       Map<Long, List<RelationRestrictionPO>> restrictionsByRelationId) {

        // 从限制条件映射表中获取当前关系ID对应的限制条件列表
        // 然后将限制条件列表转换为允许类型的集合
        Set<String> restrictions = restrictionsByRelationId.getOrDefault(relationPO.getId(), Collections.emptyList())
                .stream()
                .map(RelationRestrictionPO::getAllowedType)
                .collect(Collectors.toSet());

        // 创建并返回一个新的RelationDefinition对象
        // 使用关系名称、重写表达式和限制条件集合进行初始化
        return new RelationDefinition(
                relationPO.getRelationName(),
                relationPO.getRewriteExpression(),
                restrictions
        );
    }

    /**
     * 删除聚合内的所有子实体
     * @param storeId 域
     * @param modelId 模型Id
     */
    private void processRelationDataFromModel(String storeId, String modelId) {
        // 1. 先查询所有需要删除的子实体ID（在删除前查询）
        List<SubjectDefinitionPO> typeDefinitions = typeDefinitionPersistenceRepository.selectByModelId(storeId, modelId);
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        // 4. 删除类型定义
        typeDefinitionPersistenceRepository.deleteByModelId(storeId, modelId);
        Set<Long> typeIdSet = typeDefinitions.stream().map(SubjectDefinitionPO::getId).collect(Collectors.toSet());
        List<ModelRelationPO> relations = modelRelationPersistenceRepository.selectByTypeDefinitionId(typeIdSet);
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        // 6. 删除关系定义
        Set<Long> relationIdSet = relations.stream().map(ModelRelationPO::getId).collect(Collectors.toSet());
        modelRelationPersistenceRepository.deleteByTypeDefinitionIds(typeIdSet);
        List<RelationRestrictionPO> restrictions = relationRestrictionPersistenceRepository.selectByRelationDefinitionId(relationIdSet);
        if (CollectionUtils.isEmpty(restrictions)) {
            return;
        }

        // 8. 删除关系限制
        relationRestrictionPersistenceRepository.deleteByRelationDefinitionIds(relationIdSet);
    }


    /**
     * 批量保存主体类型定义
     *
     * @return subjectType -> id 映射
     */
    private Map<String, Long> batchSaveSubjectDefinitions(String storeId, String modelId,
                                                          List<TypeDefinitionEntity> typeDefinitions) {
        List<SubjectDefinitionPO> poList = new ArrayList<>(typeDefinitions.size());
        for (int i = 0; i < typeDefinitions.size(); i++) {
            TypeDefinitionEntity entity = typeDefinitions.get(i);
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

    /**
     * 批量保存模型关系定义
     *
     * @return (subjectType + relationName) -> id 映射
     */
    private Map<String, Long> batchSaveModelRelations(List<TypeDefinitionEntity> typeDefinitions,
                                                      Map<String, Long> typeToIdMap) {
        List<ModelRelationPO> poList = new ArrayList<>();

        for (TypeDefinitionEntity typeEntity : typeDefinitions) {
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
                po -> po.getSubjectType() + ":"+ po.getRelationName(),
                ModelRelationPO::getId));
    }


    /**
     * 批量保存类型定义及其子实体
     *
     * <p>优化策略：先收集所有待插入数据，再分层批量插入，仅 3 次数据库调用。
     * <p>由于存在外键依赖（TypeDefinition.id → ModelRelation → RelationRestriction），
     * 需要分三批插入，每批插入后获取自增主键用于下一批的外键关联。
     */
    private void batchSaveTypeDefinitions(String storeId, String modelId, List<TypeDefinitionEntity> typeDefinitions) {
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            return;
        }

        // 第一步：批量保存类型定义，返回 subjectType -> id 映射
        Map<String, Long> typeToIdMap = batchSaveSubjectDefinitions(storeId, modelId, typeDefinitions);

        // 第二步：批量保存关系定义，返回 (subjectType + relationName) -> id 映射
        Map<String, Long> relationKeyToIdMap = batchSaveModelRelations(typeDefinitions, typeToIdMap);
        if (relationKeyToIdMap.isEmpty()) {
            return;
        }

        // 第三步：批量保存关系限制
        batchSaveRelationRestrictions(typeDefinitions, relationKeyToIdMap);
    }

    /**
     * 批量保存关系类型限制
     * 该方法用于处理并保存类型定义中的关系限制信息
     * @param typeDefinitions 类型定义实体列表，包含需要处理的关系限制
     * @param relationKeyToIdMap 关系键到关系ID的映射表，用于查找关系对应的ID
     */
    private void batchSaveRelationRestrictions(List<TypeDefinitionEntity> typeDefinitions,
                                               Map<String, Long> relationKeyToIdMap) {
        // 创建关系限制持久化对象列表，用于批量保存
        List<RelationRestrictionPO> poList = new ArrayList<>();

        // 遍历类型定义列表
        for (TypeDefinitionEntity typeEntity : typeDefinitions) {
            // 获取当前类型定义中的关系定义
            Map<String, RelationDefinition> relations = typeEntity.getRelations();
            // 如果关系定义为空或为空集合，跳过当前类型定义
            if (relations == null || relations.isEmpty()) {
                continue;
            }

            // 遍历关系定义中的每个关系
            for (Map.Entry<String, RelationDefinition> entry : relations.entrySet()) {
                // 获取关系名称和关系定义对象
                String relationName = entry.getKey();
                RelationDefinition relationDef = entry.getValue();
                // 获取关系的限制集合
                Set<String> restrictions = relationDef.restrictions();

                // 如果限制集合为空或为空集合，跳过当前关系
                if (restrictions == null || restrictions.isEmpty()) {
                    continue;
                }

                // 构建关系的唯一键
                String relationKey = typeEntity.getSubjectType() + ":" + relationName;
                // 从映射表中获取关系ID
                Long relationId = relationKeyToIdMap.get(relationKey);

                // 遍历限制集合中的每个允许的类型
                for (String allowedType : restrictions) {
                    // 创建关系限制持久化对象并添加到列表中
                    poList.add(new RelationRestrictionPO(relationId, relationName, allowedType));
                }
            }
        }

        // 如果列表为空，直接返回，不执行保存操作
        if (poList.isEmpty()) {
            return;
        }
        // 批量保存关系限制
        relationRestrictionPersistenceRepository.saveBatch(poList);
    }

}
