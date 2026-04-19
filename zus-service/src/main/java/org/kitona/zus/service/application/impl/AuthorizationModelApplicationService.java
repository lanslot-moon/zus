package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IAuthorizationModelQueryRepository;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.valueobject.CursorPageResult;
import org.kitona.zus.domain.port.IModelSnapshotRenderer;
import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.assembler.AuthorizationModelAssembler;
import org.kitona.zus.service.assembler.AuthorizationTypeDefinitionAssembler;
import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型应用服务
 *
 * <p>通过应用服务编排，调用领域 Repository 与聚合根，完成授权模型的创建、查询、更新、删除。
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>所有对类型定义、关系定义的操作都通过聚合根 {@link AuthorizationModelAggregate} 进行</li>
 *   <li>不直接操作聚合内部实体的 Repository</li>
 *   <li>保证聚合的一致性边界</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class AuthorizationModelApplicationService implements IAuthorizationModelApplicationService {

    @Resource
    private IAuthorizationModelDomainRepository modelDomainRepository;

    @Resource
    private IAuthorizationModelQueryRepository modelQueryRepository;

    @Resource
    private IStoreDomainRepository storeDomainRepository;

    @Resource
    private IStoreQueryRepository storeQueryRepository;

    @Resource
    private IModelSnapshotRenderer modelSnapshotRenderer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthorizationModelResultDTO createModel(CreateModelCommand command) {
        ValidationUtil.validate(command);
        String storeId = command.getStoreId();
        ensureStoreActive(storeId);

        AuthorizationModelAggregate modelAggregate = assembleDraft(command);
        log.info("AuthorizationModelApplicationService.createModel 创建授权模型: storeId={}, modelId={}", storeId, modelAggregate.getModelId());

        modelDomainRepository.saveOrUpdateModel(modelAggregate);
        log.info("AuthorizationModelApplicationService.createModel 创建授权模型成功: storeId={}, modelId={}", storeId, modelAggregate.getModelId());

        String currentModelId = getCurrentModelId(storeId);
        return AuthorizationModelAssembler.toDTO(modelAggregate, currentModelId);
    }

    /**
     * 依据 {@link CreateModelCommand} 组装草稿态授权模型聚合：
     * 生成模型 ID、装配类型定义与条件定义。调用方负责后续持久化与状态流转。
     */
    private AuthorizationModelAggregate assembleDraft(CreateModelCommand command) {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.createWithGeneratedId(
                command.getStoreId(),
                command.getSchemaVersion(),
                command.getDescription()
        );
        aggregate.addTypeDefinitions(buildTypeDefinitions(command.getTypeDefinitions()));
        aggregate.replaceConditionDefinitions(buildConditionDefinitions(command.getConditions()));
        return aggregate;
    }

    /**
     * 根据输入的类型定义列表构建类型定义对象列表
     *
     * @param inputs 包含类型定义和关系的输入列表
     * @return 构建好的类型定义列表，如果输入为空则返回空列表
     */
    private List<TypeDefinition> buildTypeDefinitions(List<CreateModelCommand.TypeDefinitionInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return List.of();
        }

        // 每个输入都被映射为TypeDefinition对象，并处理其关系定义
        return inputs.stream()
                .map(input -> TypeDefinition.createWithRelations(input.getType(),
                        AuthorizationTypeDefinitionAssembler.toRelationDefinitions(input.getRelations())))
                .toList();
    }

    /**
     * 根据输入的条件定义列表构建条件定义对象列表
     * @param inputs 包含条件定义信息的输入列表
     * @return 构建好的条件定义对象列表，如果输入为空则返回空列表
     */
    private List<ConditionDefinition> buildConditionDefinitions(List<CreateModelCommand.ConditionDefinitionInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> ConditionDefinition.create(
                        input.getName(),
                        input.getExpression(),
                        input.getParameterSchema(),
                        input.getDescription()))
                .toList();
    }


    @Override
    public AuthorizationModelResultDTO getModel(String storeId, String modelId) {
        if (StringUtils.isBlank(storeId) || StringUtils.isBlank(modelId)) {
            return null;
        }

        Optional<AuthorizationModelAggregate> optional = modelDomainRepository.findByModelId(storeId, modelId);
        if (optional.isEmpty()) {
            log.warn("AuthorizationModelApplicationService.getModel 查询授权模型失败: storeId={}, modelId={}", storeId, modelId);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }

        String currentModelId = getCurrentModelId(storeId);
        return AuthorizationModelAssembler.toDTO(optional.get(), currentModelId);
    }

    @Override
    public AuthorizationModelResultDTO getCurrentModel(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return null;
        }
        log.info("AuthorizationModelApplicationService.getCurrentModel 查询当前授权模型: storeId={}", storeId);
        String currentModelId = getCurrentModelId(storeId);
        if (StringUtils.isBlank(currentModelId)) {
            log.info("AuthorizationModelApplicationService.getCurrentModel Store 未绑定模型: storeId={}", storeId);
            return null;
        }

        Optional<AuthorizationModelAggregate> optional = modelDomainRepository.findByModelId(storeId, currentModelId);
        if (optional.isEmpty()) {
            log.warn("AuthorizationModelApplicationService.getCurrentModel 查询授权模型失败, modelId={}", currentModelId);
            return null;
        }
        return AuthorizationModelAssembler.toDTO(optional.get(), currentModelId);
    }

    @Override
    public PageResultDTO<AuthorizationModelResultDTO> listModels(ListModelsQuery query) {
        ValidationUtil.validate(query);

        CursorPageResult<AuthorizationModelView> pageResult = modelQueryRepository.findPageViewByCursor(
                query.getStoreId(),
                query.getStatus(),
                query.getPageToken(),
                query.getEffectivePageSize()
        );

        if (pageResult.isEmpty()) {
            log.info("AuthorizationModelApplicationService.listModels 查询授权模型列表为空: storeId={}", query.getStoreId());
            return PageResultDTO.empty();
        }

        String currentModelId = getCurrentModelId(query.getStoreId());
        List<AuthorizationModelResultDTO> resultList = AuthorizationModelAssembler.toViewDTOList(pageResult.data(), currentModelId);
        return PageResultDTO.of(resultList, pageResult.nextPageToken());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        ensureStoreActive(storeId);
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId);

        model.publish(modelSnapshotRenderer.render(model));
        boolean result = modelDomainRepository.saveOrUpdateModel(model);
        if (!result) {
            log.info("发布授权模型失败: storeId={}, modelId={}", storeId, modelId);
            return false;
        }

        log.info("发布授权模型成功: storeId={}, modelId={}（需调用 activate 接口激活）", storeId, modelId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        StoreAggregate store = ensureStoreActive(storeId);
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId);
        log.info("AuthorizationModelApplicationService.activateModel 激活授权模型: storeId={}, modelId={}, status={}", storeId, modelId, model.getStatus());

        if (!model.isPublished()) {
            log.warn("AuthorizationModelApplicationService.activateModel 只能激活已发布的模型");
            throw new ApplicationException(IError.DATA_STATUS_ERROR);
        }

        if (modelId.equals(store.getCurrentModelId())) {
            log.info("激活授权模型时发现已是当前模型: storeId={}, modelId={}", storeId, modelId);
            return true;
        }

        store.updateCurrentModel(modelId);
        boolean updated = storeDomainRepository.saveOrUpdateStore(store);
        if (!updated) {
            log.warn("激活授权模型失败，Store 当前模型指针更新未生效: storeId={}, modelId={}", storeId, modelId);
            return false;
        }

        log.info("激活授权模型成功，事务内已完成 Store 当前模型切换: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deprecateModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        ensureStoreActive(storeId);
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId);

        model.deprecate();
        boolean result = modelDomainRepository.saveOrUpdateModel(model);
        if (!result) {
            log.info("废弃授权模型失败: storeId={}, modelId={}", storeId, modelId);
            return false;
        }

        log.info("废弃授权模型成功: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    public boolean deleteModel(String storeId, String modelId) {
        if (StringUtils.isBlank(storeId) || StringUtils.isBlank(modelId)) {
            return false;
        }
        ensureStoreActive(storeId);
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId);

        if (!model.isDraft()) {
            log.warn("只能删除草稿状态的模型: storeId={}, modelId={}, status={}", storeId, modelId, model.getStatus());
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }

        return modelDomainRepository.deleteDraftModel(storeId, modelId);
    }

    /**
     * 获取 Store 当前生效的模型ID
     */
    private String getCurrentModelId(String storeId) {
        Optional<StoreView> optional = storeQueryRepository.findViewByStoreId(storeId);
        return optional.map(StoreView::currentModelId).orElse(null);
    }

    /**
     * 确保商店处于激活状态
     * @param storeId 商店ID
     * @return 激活状态的StoreAggregate对象
     * @throws ApplicationException 当商店不存在或未激活时抛出
     */
    private StoreAggregate ensureStoreActive(String storeId) {
        StoreAggregate storeAggregate = storeDomainRepository.findByStoreId(storeId).orElse(null);
        if (storeAggregate == null) {
            log.warn("Store不存在: storeId={}", storeId);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }
        if (!storeAggregate.isActive()) {
            log.warn("Store 未激活，无法操作模型,模型信息:{}", JacksonUtil.toJSONString(storeAggregate));
            throw new ApplicationException(IError.DATA_STATUS_ERROR);
        }
        return storeAggregate;
    }

    /**
     * 根据店铺ID和模型ID加载授权模型聚合，如果不存在则抛出异常
     *
     * @param storeId 店铺ID，标识特定的店铺
     * @param modelId 模型ID，标识特定的授权模型
     * @return AuthorizationModelAggregate 返回找到的授权模型聚合
     * @throws ApplicationException 当查询不到对应的授权模型时抛出异常
     */
    private AuthorizationModelAggregate loadModelOrThrow(String storeId, String modelId) {
        AuthorizationModelAggregate model = modelDomainRepository.findByModelId(storeId, modelId).orElse(null);
        if (model != null) {
            return model;
        }
        log.warn("AuthorizationModelApplicationService. 查询授权模型失败: storeId={}, modelId={}", storeId, modelId);
        throw new ApplicationException(IError.DATA_NOT_EXIST);
    }
}
