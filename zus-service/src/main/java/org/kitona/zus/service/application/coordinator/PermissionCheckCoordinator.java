package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 权限检查编排器
 *
 * <p>应用层内部支撑组件，负责编排领域服务完成权限检查核心流程。
 */
@Slf4j
@Component
public class PermissionCheckCoordinator {

    /**
     * 权限评估上下文加载器，用于复用 Store/Model/CompiledModel/Preflight 准备流程。
     */
    @Resource
    private PermissionEvaluationContextLoader evaluationContextLoader;

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

        PermissionEvaluationContext evaluationContext = evaluationContextLoader.load(storeId, object, zookie);
        if (evaluationContext.isAbnormal()) {
            return PermissionCheckResult.abnormal(evaluationContext.abnormalStatus());
        }

        if (evaluationContext.isPreflightDenied()) {
            log.debug("权限检查前置检查拒绝: storeId={}, object={}, relation={}, subject={}, reason={}",
                    storeId, object, relation, subject, evaluationContext.preflightFailureReason().name());
            return PermissionCheckResult.denied();
        }

        EvaluationRequest request = EvaluationRequest.of(storeId, subject, object, relation, zookie, context);
        boolean result = permissionCheckEvaluator.check(evaluationContext.compiledModel(), request);
        log.debug("权限检查结果: {}", result);
        return result ? PermissionCheckResult.allowed() : PermissionCheckResult.denied();
    }
}
