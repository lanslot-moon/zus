package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 权限评估上下文加载器。
 *
 * <p>该组件统一承载 Check 与 Explain 共同需要的应用层准备流程：
 * 加载 Store、定位当前模型、加载模型聚合、编译模型并执行 preflight。
 * Coordinator 只消费准备好的上下文，不再重复关心模型装载与缓存细节。
 */
@Slf4j
@Component
public class PermissionEvaluationContextLoader {

    /**
     * Store 查询仓储，用于加载当前 store 视图和当前模型指针。
     */
    @Resource
    private IStoreQueryPort storeQueryRepository;

    /**
     * 授权模型仓储，用于加载当前激活模型聚合。
     */
    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    /**
     * 编译模型端口，用于把结构化模型聚合编译为 evaluator 可执行模型。
     */
    @Resource
    private ICompiledModelCompiler compiledModelCompiler;

    /**
     * 编译模型缓存，用于复用同一 store/model 下的编译产物。
     */
    @Resource
    private ICompiledModelCache compiledModelCache;

    /**
     * 权限评估前置检查组件，用于在进入 evaluator 前执行低成本快速失败。
     */
    @Resource
    private PermissionEvaluationPreflight permissionEvaluationPreflight;

    /**
     * 加载一次权限评估所需的共享上下文。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param zookie   一致性 token
     * @return 权限评估上下文
     */
    PermissionEvaluationContext load(String storeId, ObjectRef object, Zookie zookie) {
        Optional<StoreView> storeOpt = storeQueryRepository.findViewByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            log.warn("store 不存在: {}", storeId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.STORE_NOT_FOUND);
        }

        StoreView storeView = storeOpt.get();
        String currentModelId = storeView.currentModelId();
        if (StringUtils.isBlank(currentModelId)) {
            log.warn("store 未绑定授权模型: {}", storeId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_BOUND);
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelRepository.findById(AuthorizationModelId.of(storeId, currentModelId));
        if (modelOpt.isEmpty()) {
            log.warn("授权模型不存在: storeId={}, modelId={}", storeId, currentModelId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_FOUND);
        }

        AuthorizationModelAggregate aggregate = modelOpt.get();
        if (aggregate.getTypeDefinitions().isEmpty()) {
            log.warn("授权模型无类型定义: storeId={}, modelId={}", storeId, currentModelId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_INVALID);
        }

        CompiledAuthorizationModel compiledModel;
        try {
            compiledModel = loadCompiledModel(storeId, currentModelId, aggregate);
        } catch (RuntimeException ex) {
            log.warn("授权模型结构无效，无法构建鉴权图: storeId={}, modelId={}", storeId, currentModelId, ex);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_INVALID);
        }

        EvaluationExplainReason failureReason = permissionEvaluationPreflight.inspect(storeId, object, zookie);
        return PermissionEvaluationContext.ready(storeView, compiledModel, failureReason);
    }

    /**
     * 从缓存或编译器获取编译模型。
     *
     * @param storeId        Store 标识
     * @param currentModelId 当前模型 ID
     * @param aggregate      授权模型聚合
     * @return 已编译授权模型
     */
    private CompiledAuthorizationModel loadCompiledModel(String storeId, String currentModelId,
                                                         AuthorizationModelAggregate aggregate) {
        return compiledModelCache.get(storeId, currentModelId)
                .orElseGet(() -> {
                    CompiledAuthorizationModel model = compiledModelCompiler.compile(aggregate);
                    compiledModelCache.put(storeId, currentModelId, model);
                    return model;
                });
    }
}

/**
 * 权限评估共享上下文。
 *
 * <p>该类型只在 coordinator 包内流转，使用包私有普通类而不是 record，
 * 以便精确控制访问器可见性，避免内部协作类型通过自动生成方法泄露到公共 API。
 */
final class PermissionEvaluationContext {

    /**
     * 异常状态；为空表示上下文准备成功。
     */
    private final PermissionCheckStatus abnormalStatus;

    /**
     * Store 读侧视图。
     */
    private final StoreView storeView;

    /**
     * 已编译授权模型。
     */
    private final CompiledAuthorizationModel compiledModel;

    /**
     * 前置检查失败原因；为空表示前置检查通过。
     */
    private final EvaluationExplainReason preflightFailureReason;

    /**
     * 创建权限评估共享上下文。
     *
     * @param abnormalStatus 异常状态
     * @param storeView      Store 读侧视图
     * @param compiledModel  已编译授权模型
     * @param preflightFailureReason 前置检查失败原因
     */
    private PermissionEvaluationContext(PermissionCheckStatus abnormalStatus, StoreView storeView,
                                        CompiledAuthorizationModel compiledModel,
                                        EvaluationExplainReason preflightFailureReason) {
        this.abnormalStatus = abnormalStatus;
        this.storeView = storeView;
        this.compiledModel = compiledModel;
        this.preflightFailureReason = preflightFailureReason;
    }

    /**
     * 创建异常上下文。
     *
     * @param status 异常状态
     * @return 异常上下文
     */
    static PermissionEvaluationContext abnormal(PermissionCheckStatus status) {
        return new PermissionEvaluationContext(status, null, null, null);
    }

    /**
     * 创建可执行上下文。
     *
     * @param storeView     Store 读侧视图
     * @param compiledModel 已编译授权模型
     * @param preflightFailureReason 前置检查失败原因
     * @return 可执行上下文
     */
    static PermissionEvaluationContext ready(StoreView storeView, CompiledAuthorizationModel compiledModel,
                                             EvaluationExplainReason preflightFailureReason) {
        return new PermissionEvaluationContext(null, storeView, compiledModel, preflightFailureReason);
    }

    /**
     * 判断上下文准备是否异常。
     *
     * @return 存在异常状态返回 {@code true}
     */
    boolean isAbnormal() {
        return abnormalStatus != null;
    }

    /**
     * 判断是否被前置检查拒绝。
     *
     * @return 前置检查拒绝返回 {@code true}
     */
    boolean isPreflightDenied() {
        return preflightFailureReason != null && !preflightFailureReason.isSuccess();
    }

    /**
     * 返回上下文准备异常状态。
     *
     * @return 异常状态
     */
    PermissionCheckStatus abnormalStatus() {
        return abnormalStatus;
    }

    /**
     * 返回 Store 读侧视图。
     *
     * @return Store 读侧视图
     */
    StoreView storeView() {
        return storeView;
    }

    /**
     * 返回已编译授权模型。
     *
     * @return 已编译授权模型
     */
    CompiledAuthorizationModel compiledModel() {
        return compiledModel;
    }

    /**
     * 返回前置检查失败原因。
     *
     * @return 前置检查失败原因
     */
    EvaluationExplainReason preflightFailureReason() {
        return preflightFailureReason;
    }
}
