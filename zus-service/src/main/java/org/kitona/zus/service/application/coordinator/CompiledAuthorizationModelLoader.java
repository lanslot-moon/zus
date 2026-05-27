package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.service.exception.ApplicationException;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 编译授权模型加载器。
 *
 * <p>该组件统一承载应用层对 {@link CompiledAuthorizationModel} 的加载、缓存和编译流程。
 * Check、Explain、ListObjects、ListSubjects 等执行入口都应通过这里获取编译模型，
 * 避免不同用例各自实现模型解析后产生语义分叉。
 */
@Component
public class CompiledAuthorizationModelLoader {

    /**
     * Store 查询端口，用于在未显式指定模型时解析当前激活模型。
     */
    @Resource
    private IStoreQueryPort storeQueryRepository;

    /**
     * 授权模型聚合仓储，用于加载结构化模型真相。
     */
    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    /**
     * 编译模型端口，用于把结构化模型转换为 evaluator 可执行模型。
     */
    @Resource
    private ICompiledModelCompiler compiledModelCompiler;

    /**
     * 编译模型缓存，用于复用同一 store/model 下的编译产物。
     */
    @Resource
    private ICompiledModelCache compiledModelCache;

    /**
     * 加载当前激活模型或调用方显式指定的模型。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @return 已编译授权模型
     */
    public CompiledAuthorizationModel load(String storeId, String authorizationModelId) {
        String modelId = resolveModelId(storeId, authorizationModelId);
        return findByModelId(storeId, modelId)
                .orElseThrow(() -> new ApplicationException(IError.DATA_NOT_EXIST, "授权模型不存在: " + modelId));
    }

    /**
     * 按模型标识加载编译模型。
     *
     * @param storeId Store 标识
     * @param modelId 模型标识
     * @return 已编译授权模型；模型不存在时返回 empty
     */
    Optional<CompiledAuthorizationModel> findByModelId(String storeId, String modelId) {
        Optional<CompiledAuthorizationModel> cachedModel = compiledModelCache.get(storeId, modelId);
        if (cachedModel.isPresent()) {
            return cachedModel;
        }

        Optional<AuthorizationModelAggregate> aggregateOptional = modelRepository.findById(AuthorizationModelId.of(storeId, modelId));
        if (aggregateOptional.isEmpty()) {
            return Optional.empty();
        }

        AuthorizationModelAggregate aggregate = aggregateOptional.get();
        if (aggregate.getTypeDefinitions().isEmpty()) {
            throw new ApplicationException(IError.DATA_STATUS_ERROR, "授权模型无类型定义: " + modelId);
        }

        CompiledAuthorizationModel model = compiledModelCompiler.compile(aggregate);
        compiledModelCache.put(storeId, modelId, model);
        return Optional.of(model);
    }

    /**
     * 解析本次执行使用的授权模型 ID。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @return 模型标识
     */
    private String resolveModelId(String storeId, String authorizationModelId) {
        if (StringUtils.isNotBlank(authorizationModelId)) {
            return authorizationModelId;
        }
        Optional<StoreView> optional = storeQueryRepository.findViewByStoreId(storeId);
        if (optional.isEmpty()) {
            throw new ApplicationException(IError.DATA_NOT_EXIST, "Store 不存在: " + storeId);
        }
        StoreView storeView = optional.get();
        if (StringUtils.isBlank(storeView.currentModelId())) {
            throw new ApplicationException(IError.DATA_STATUS_ERROR, "未指定授权模型且 Store 未绑定当前模型: " + storeId);
        }
        return storeView.currentModelId();
    }
}
