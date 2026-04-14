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
import org.kitona.zus.domain.authorization.model.RelationDefinition;
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
import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.exception.ApplicationException;
import org.kitona.zus.service.factory.AuthorizationModelDraftFactory;
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
    public boolean createModel(CreateModelCommand command) {
        ValidationUtil.validate(command);
        String storeId = command.getStoreId();
        ensureStoreActive(storeId, "createModel");

        AuthorizationModelAggregate modelAggregate = AuthorizationModelDraftFactory.create(command);

        log.info("AuthorizationModelApplicationService.createModel 创建授权模型: storeId={}, modelId={}", storeId, modelAggregate.getModelId());

        if (CollectionUtils.isEmpty(command.getTypeDefinitions())) {
            modelDomainRepository.saveOrUpdateModel(modelAggregate);
            log.info("AuthorizationModelApplicationService.createModel 无关系定义,创建授权模型成功: storeId={}, modelId={}", storeId, modelAggregate.getModelId());
            return true;
        }

        modelDomainRepository.saveOrUpdateModel(modelAggregate);
        log.info("AuthorizationModelApplicationService.createModel 创建授权模型成功: storeId={}, modelId={}", storeId, modelAggregate.getModelId());
        return true;
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
        List<AuthorizationModelResultDTO> resultList =
                AuthorizationModelAssembler.toViewDTOList(pageResult.data(), currentModelId);
        return PageResultDTO.of(resultList, pageResult.nextPageToken());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishModel(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            return false;
        }
        ensureStoreActive(storeId, "publishModel");
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId, "publishModel");

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
        StoreAggregate store = ensureStoreActive(storeId, "activateModel");
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId, "activateModel");

        if (!model.isPublished()) {
            log.warn("AuthorizationModelApplicationService.activateModel 只能激活已发布的模型: storeId={}, modelId={}, status={}",
                    storeId, modelId, model.getStatus());
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
        ensureStoreActive(storeId, "deprecateModel");
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId, "deprecateModel");

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
        ensureStoreActive(storeId, "deleteModel");
        AuthorizationModelAggregate model = loadModelOrThrow(storeId, modelId, "deleteModel");

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
        return storeQueryRepository.findViewByStoreId(storeId)
                .map(StoreView::currentModelId)
                .orElse(null);
    }

    private StoreAggregate ensureStoreActive(String storeId, String action) {
        StoreAggregate storeAggregate = storeDomainRepository.findByStoreId(storeId).orElse(null);
        if (storeAggregate == null) {
            log.warn("{} Store不存在: storeId={}", action, storeId);
            throw new ApplicationException(IError.DATA_NOT_EXIST);
        }
        if (!storeAggregate.isActive()) {
            log.warn("{} Store 未激活，无法操作模型,模型信息:{}", action, JacksonUtil.toJSONString(storeAggregate));
            throw new ApplicationException(IError.DATA_STATUS_ERROR);
        }
        return storeAggregate;
    }

    private AuthorizationModelAggregate loadModelOrThrow(String storeId, String modelId, String action) {
        AuthorizationModelAggregate model = modelDomainRepository.findByModelId(storeId, modelId).orElse(null);
        if (model != null) {
            return model;
        }
        log.warn("AuthorizationModelApplicationService.{} 查询授权模型失败: storeId={}, modelId={}", action, storeId, modelId);
        throw new ApplicationException(IError.DATA_NOT_EXIST);
    }
}
