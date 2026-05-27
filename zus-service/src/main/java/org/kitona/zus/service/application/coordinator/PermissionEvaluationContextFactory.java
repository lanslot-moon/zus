package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 权限评估上下文工厂。
 *
 * <p>该组件统一承载 Check 与 Explain 共同需要的应用层准备流程：
 * 加载 Store、定位当前模型、获取编译模型并执行对象事实快速失败。
 * Coordinator 只消费准备好的上下文，不再重复关心模型装载与缓存细节。
 */
@Slf4j
@Component
public class PermissionEvaluationContextFactory {

    /**
     * Store 查询仓储，用于加载当前 store 视图和当前模型指针。
     */
    @Resource
    private IStoreQueryPort storeQueryRepository;

    /**
     * tuple 读侧查询端口，用于判断目标对象是否已经出现在授权事实层。
     */
    @Resource
    private ITupleQueryPort tupleQueryPort;

    /**
     * 编译授权模型加载器，用于统一模型聚合加载、缓存和编译逻辑。
     */
    @Resource
    private CompiledAuthorizationModelLoader compiledAuthorizationModelLoader;

    /**
     * 创建一次权限评估所需的共享上下文。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param zookie   一致性 token
     * @return 权限评估上下文
     */
    PermissionEvaluationContext create(String storeId, ObjectRef object, Zookie zookie) {
        return create(storeId, null, object, zookie);
    }

    /**
     * 创建一次权限评估所需的共享上下文。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @param object               授权对象
     * @param zookie               一致性 token
     * @return 权限评估上下文
     */
    PermissionEvaluationContext create(String storeId, String authorizationModelId, ObjectRef object, Zookie zookie) {
        Optional<StoreView> storeOpt = storeQueryRepository.findViewByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            log.warn("store 不存在: {}", storeId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.STORE_NOT_FOUND);
        }

        StoreView storeView = storeOpt.get();
        String modelId = resolveModelId(authorizationModelId, storeView);
        if (StringUtils.isBlank(modelId)) {
            log.warn("store 未绑定授权模型: {}", storeId);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_BOUND);
        }

        CompiledAuthorizationModel compiledModel;
        try {
            Optional<CompiledAuthorizationModel> modelOptional = compiledAuthorizationModelLoader.findByModelId(storeId, modelId);
            if (modelOptional.isEmpty()) {
                log.warn("授权模型不存在: storeId={}, modelId={}", storeId, modelId);
                return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_FOUND);
            }
            compiledModel = modelOptional.get();
        } catch (RuntimeException ex) {
            log.warn("授权模型结构无效，无法构建鉴权图: storeId={}, modelId={}", storeId, modelId, ex);
            return PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_INVALID);
        }

        EvaluationExplainReason failureReason = this.resolvePreflightFailureReason(storeId, object, zookie);
        return PermissionEvaluationContext.ready(storeView, compiledModel, failureReason);
    }

    /**
     * 解析本次评估使用的授权模型标识。
     *
     * @param authorizationModelId 调用方显式指定的授权模型标识
     * @param storeView            Store 读侧视图
     * @return 授权模型标识
     */
    private String resolveModelId(String authorizationModelId, StoreView storeView) {
        if (StringUtils.isNotBlank(authorizationModelId)) {
            return authorizationModelId;
        }
        return storeView.currentModelId();
    }

    /**
     * 解析进入 evaluator 前的快速失败原因。
     * 这里只判断目标对象在授权事实层是否出现过，不检查模型 type/relation。
     * 模型语义问题必须交给 evaluator 解释，避免应用层提前抢占领域执行链路。
     *
     * @param storeId Store 标识
     * @param object  授权对象
     * @param zookie  一致性 token
     * @return 快速失败原因；返回 {@code null} 表示允许进入 evaluator
     */
    private EvaluationExplainReason resolvePreflightFailureReason(String storeId, ObjectRef object, Zookie zookie) {
        boolean existed = tupleQueryPort.existsObjectFact(storeId, object, zookie.getVersion());
        return existed ? null : BusinessEvidenceReason.OBJECT_FACT_NOT_FOUND;
    }
}
