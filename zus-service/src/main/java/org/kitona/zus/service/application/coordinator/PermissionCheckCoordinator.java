package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 权限检查编排器
 *
 * <p>应用层内部支撑组件，负责编排领域服务完成权限检查核心流程。
 */
@Slf4j
@Component
public class PermissionCheckCoordinator {

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
     * 单点权限证明领域服务，只负责执行明确的 subject-object-relation Check 命题。
     */
    @Resource
    private PermissionCheckEvaluator permissionCheckEvaluator;

    /**
     * 使用默认上下文执行权限检查。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param relation 关系名
     * @param subject  授权主体
     * @param zookie   一致性版本
     * @return 权限检查结果
     */
    public PermissionCheckResult execute(String storeId, ObjectRef object, String relation,
                                         Subject subject, Zookie zookie) {
        return execute(storeId, object, relation, subject, zookie, null);
    }

    /**
     * 执行权限检查用例编排。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param relation 关系名
     * @param subject  授权主体
     * @param zookie   一致性版本
     * @param context  条件求值上下文
     * @return 权限检查结果
     */
    public PermissionCheckResult execute(String storeId, ObjectRef object, String relation,
                                         Subject subject, Zookie zookie, Map<String, Object> context) {
        log.debug("开始权限检查: storeId={}, object={}, relation={}, subject={}", storeId, object, relation, subject);

        Optional<StoreView> storeOpt = storeQueryRepository.findViewByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            log.warn("store 不存在: {}", storeId);
            return PermissionCheckResult.storeNotFound();
        }

        String currentModelId = storeOpt.get().currentModelId();
        if (StringUtils.isBlank(currentModelId)) {
            log.warn("store 未绑定授权模型: {}", storeId);
            return PermissionCheckResult.modelNotBound();
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelRepository.findById(AuthorizationModelId.of(storeId, currentModelId));
        if (modelOpt.isEmpty()) {
            log.warn("授权模型不存在: storeId={}, modelId={}", storeId, currentModelId);
            return PermissionCheckResult.modelNotFound();
        }

        AuthorizationModelAggregate aggregate = modelOpt.get();
        if (aggregate.getTypeDefinitions().isEmpty()) {
            log.warn("授权模型无类型定义: storeId={}, modelId={}", storeId, currentModelId);
            return PermissionCheckResult.modelInvalid();
        }

        CompiledAuthorizationModel compiledModel;
        try {
            compiledModel = compiledModelCache.get(storeId, currentModelId)
                    .orElseGet(() -> {
                        CompiledAuthorizationModel model = compiledModelCompiler.compile(aggregate);
                        compiledModelCache.put(storeId, currentModelId, model);
                        return model;
                    });
        } catch (RuntimeException ex) {
            log.warn("授权模型结构无效，无法构建鉴权图: storeId={}, modelId={}", storeId, currentModelId, ex);
            return PermissionCheckResult.modelInvalid();
        }

        EvaluationRequest request = EvaluationRequest.of(storeId, subject, object, relation, zookie, context);
        boolean result = permissionCheckEvaluator.check(compiledModel, request);
        log.debug("权限检查结果: {}", result);
        return result ? PermissionCheckResult.allowed() : PermissionCheckResult.denied();
    }
}
