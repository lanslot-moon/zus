package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.service.application.IModelSchemaApplicationService;
import org.kitona.zus.service.assembler.TypeDefinitionAssembler;
import org.kitona.zus.service.dto.command.AddRelationCommand;
import org.kitona.zus.service.dto.command.AddTypeDefinitionCommand;
import org.kitona.zus.service.dto.response.RelationResultDTO;
import org.kitona.zus.service.dto.response.TypeDefinitionResultDTO;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 授权模型结构应用服务实现
 *
 * <p>实现「管理授权模型结构」这一领域能力，包含类型定义与关系定义的完整操作。
 * 所有写操作通过聚合根保证一致性，仅草稿态模型可编辑。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Service
public class ModelSchemaApplicationService implements IModelSchemaApplicationService {

    @Resource
    private IAuthorizationModelDomainRepository modelDomainRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTypeDefinition(AddTypeDefinitionCommand command) {
        ValidationUtil.validate(command);

        AuthorizationModelAggregate model = this.loadForEdit(command.getStoreId(), command.getModelId());

        List<RelationDefinition> relationDefs = TypeDefinitionAssembler.toRelationDefinitions(command.getRelations(), command.getRelationRestrictions());
        TypeDefinitionEntity typeEntity = TypeDefinitionEntity.createWithRelations(command.getType(), relationDefs);

        try {
            model.addTypeDefinition(typeEntity);
        } catch (IllegalArgumentException e) {
            log.warn("addTypeDefinition 类型定义已存在: {}", e.getMessage());
            throw new ApplicationException(IError.DATA_EXIST_ERROR);
        }

        modelDomainRepository.saveOrUpdateModel(model);
        log.info("添加类型定义成功: storeId={}, modelId={}, type={}", command.getStoreId(), command.getModelId(), command.getType());
        return true;
    }

    @Override
    public List<TypeDefinitionResultDTO> listTypeDefinitions(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            log.warn("listTypeDefinitions 参数为空: storeId={}, modelId={}", storeId, modelId);
            return Collections.emptyList();
        }
        Optional<AuthorizationModelAggregate> modelOpt = modelDomainRepository.findByModelId(storeId, modelId);
        if (modelOpt.isEmpty()) {
            log.warn("listTypeDefinitions 模型不存在: storeId={}, modelId={}", storeId, modelId);
            return Collections.emptyList();
        }
        List<TypeDefinitionEntity> typeDefinitions = modelOpt.get().getTypeDefinitions();
        if (CollectionUtils.isEmpty(typeDefinitions)) {
            log.info("listTypeDefinitions 模型无类型定义: storeId={}, modelId={}", storeId, modelId);
            return Collections.emptyList();
        }
        return TypeDefinitionAssembler.toDTOList(typeDefinitions);
    }

    @Override
    public TypeDefinitionResultDTO getTypeDefinition(String storeId, String modelId, String type) {
        if (StringUtils.isAnyBlank(storeId, modelId, type)) {
            log.warn("getTypeDefinition 参数为空: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return null;
        }

        Optional<AuthorizationModelAggregate> optional = modelDomainRepository.findByModelId(storeId, modelId);
        if (optional.isEmpty()) {
            log.warn("getTypeDefinition 模型不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        Optional<TypeDefinitionEntity> typeDefinition = optional.get().getTypeDefinition(type);
        if (typeDefinition.isEmpty()) {
            log.warn("getTypeDefinition 类型定义不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        return TypeDefinitionAssembler.toDTO(typeDefinition.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTypeDefinition(String storeId, String modelId, String type) {
        if (StringUtils.isAnyBlank(storeId, modelId, type)) {
            log.warn("deleteTypeDefinition 参数为空: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return false;
        }

        log.info("deleteTypeDefinition 删除类型定义: storeId={}, modelId={}, type={}", storeId, modelId, type);
        AuthorizationModelAggregate model = this.loadForEdit(storeId, modelId);
        Optional<TypeDefinitionEntity> removed = model.removeTypeDefinition(type);
        if (removed.isEmpty()) {
            log.warn("deleteTypeDefinition 类型定义不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return false;
        }
        modelDomainRepository.saveOrUpdateModel(model);
        log.info("删除类型定义成功: storeId={}, modelId={}, type={}", storeId, modelId, type);
        return true;
    }

    // ==================== 关系定义 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRelation(AddRelationCommand command) {
        ValidationUtil.validate(command);

        AuthorizationModelAggregate model = this.loadForEdit(command.getStoreId(), command.getModelId());
        Optional<TypeDefinitionEntity> typeDefinition = model.getTypeDefinition(command.getType());
        if (typeDefinition.isEmpty()) {
            log.warn("addRelation 类型定义不存在: type={}", command.getType());
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        RelationDefinition relationDef = buildRelationDefinition(command);
        try {
            model.addRelationToType(command.getType(), relationDef);
        } catch (IllegalArgumentException e) {
            log.warn("addRelation 添加失败:", e);
            throw new ApplicationException(IError.DATA_EXIST_ERROR);
        }

        modelDomainRepository.saveOrUpdateModel(model);
        log.info("添加关系定义成功: type={}, relationName={}", command.getType(), command.getRelationName());
        return true;
    }

    @Override
    public List<RelationResultDTO> listRelations(String storeId, String modelId, String type) {
        if (StringUtils.isAnyBlank(storeId, modelId, type)) {
            log.warn("listRelations 参数为空: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return Collections.emptyList();
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelDomainRepository.findByModelId(storeId, modelId);
        if (modelOpt.isEmpty()) {
            log.warn("listRelations 模型不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return Collections.emptyList();
        }

        Optional<TypeDefinitionEntity> typeOpt = modelOpt.get().getTypeDefinition(type);
        if (typeOpt.isEmpty()) {
            log.warn("listRelations 类型定义不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return Collections.emptyList();
        }

        TypeDefinitionEntity typeEntity = typeOpt.get();
        Map<String, RelationDefinition> relations = typeEntity.getRelations();
        if (MapUtils.isEmpty(relations)) {
            log.warn("listRelations 关系定义不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return Collections.emptyList();
        }
        return TypeDefinitionAssembler.toRelationDTOList(typeEntity.getId(), relations);
    }

    @Override
    public RelationResultDTO getRelation(String storeId, String modelId, String type, String relationName) {
        log.info("getRelation 获取关系定义: storeId={}, modelId={}, type={}, relationName={}", storeId, modelId, type, relationName);
        if (StringUtils.isAnyBlank(storeId, modelId, type, relationName)) {
            return null;
        }
        Optional<AuthorizationModelAggregate> modelOpt = modelDomainRepository.findByModelId(storeId, modelId);
        if (modelOpt.isEmpty()) {
            log.warn("getRelation 模型不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return null;
        }

        Optional<TypeDefinitionEntity> typeOpt = modelOpt.get().getTypeDefinition(type);
        if (typeOpt.isEmpty()) {
            log.warn("getRelation 类型定义不存在: storeId={}, modelId={}, type={}", storeId, modelId, type);
            return null;
        }

        RelationDefinition relationDef = typeOpt.get().getRelation(relationName);
        if (relationDef == null) {
            log.warn("getRelation 关系定义不存在: type={}, relationName={}", type, relationName);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }
        return TypeDefinitionAssembler.toRelationDTO(typeOpt.get().getId(), relationDef);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRelation(AddRelationCommand command) {
        ValidationUtil.validate(command);

        AuthorizationModelAggregate model = this.loadForEdit(command.getStoreId(), command.getModelId());
        Optional<TypeDefinitionEntity> typeDefinition = model.getTypeDefinition(command.getType());
        if (typeDefinition.isEmpty()) {
            log.warn("updateRelation 类型定义不存在，参数:{}", JacksonUtil.toJSONString(command));
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        RelationDefinition existingRelation = typeDefinition.get().getRelation(command.getRelationName());
        if (existingRelation == null) {
            log.warn("updateRelation 关系定义不存在，参数:{}", JacksonUtil.toJSONString(command));
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        RelationDefinition newRelationDef = buildUpdatedRelationDefinition(command, existingRelation);
        model.updateRelationInType(command.getType(), newRelationDef);
        modelDomainRepository.saveOrUpdateModel(model);
        log.info("更新关系定义成功: type={}, relationName={}", command.getType(), command.getRelationName());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRelation(String storeId, String modelId, String type, String relationName) {
        if (StringUtils.isAnyBlank(storeId, modelId, type, relationName)) {
            return false;
        }
        AuthorizationModelAggregate model = this.loadForEdit(storeId, modelId);
        if (model.removeRelationFromType(type, relationName).isEmpty()) {
            log.warn("关系定义不存在或类型不存在: storeId={}, modelId={}, type={}, relationName={}", storeId, modelId, type, relationName);
            return false;
        }
        modelDomainRepository.saveOrUpdateModel(model);
        log.info("删除关系定义成功: type={}, relationName={}", type, relationName);
        return true;
    }

    /**
     * 加载聚合根用于编辑操作
     *
     * <p>校验模型是否存在且处于草稿状态（可编辑）
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 可编辑的聚合根
     * @throws ApplicationException 模型不存在或不可编辑时抛出
     */
    private AuthorizationModelAggregate loadForEdit(String storeId, String modelId) {
        Optional<AuthorizationModelAggregate> modelOptionl = modelDomainRepository.findByModelId(storeId, modelId);
        if (modelOptionl.isEmpty()) {
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        AuthorizationModelAggregate model = modelOptionl.get();
        if (!model.isDraft()) {
            log.warn("只能操作草稿状态的模型: storeId={}, modelId={}, status={}", storeId, modelId, model.getStatus());
            throw new ApplicationException(IError.DATA_STATUS_ERROR);
        }

        return model;
    }


    // ==================== 私有方法 ====================

    private RelationDefinition buildRelationDefinition(AddRelationCommand command) {
        String rewriteExpression = StringUtils.isNotBlank(command.getRewriteExpression())
                ? command.getRewriteExpression()
                : "self";
        Set<String> allowedTypes = CollectionUtils.isNotEmpty(command.getAllowedTypes())
                ? new HashSet<>(command.getAllowedTypes())
                : Collections.emptySet();
        return new RelationDefinition(command.getRelationName(), rewriteExpression, allowedTypes);
    }

    private RelationDefinition buildUpdatedRelationDefinition(AddRelationCommand command, RelationDefinition existing) {
        String newExpression = StringUtils.isNotBlank(command.getRewriteExpression())
                ? command.getRewriteExpression()
                : existing.rewriteExpression();
        Set<String> newRestrictions = CollectionUtils.isNotEmpty(command.getAllowedTypes())
                ? new HashSet<>(command.getAllowedTypes())
                : existing.restrictions();
        return new RelationDefinition(command.getRelationName(), newExpression, newRestrictions);
    }
}
