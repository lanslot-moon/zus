package org.kitona.zus.service.application.coordinator;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;

/**
 * 权限评估共享上下文。
 *
 * <p>该类型只在 coordinator 包内流转，用于承载 Check 与 Explain 共用的 Store 视图、
 * 编译模型和进入 evaluator 前的快速失败原因。它只表达数据，不构造 explain 树，
 * 避免上下文对象混入展示/调试结构组装职责。
 *
 * @param abnormalStatus         异常状态；为空表示上下文准备成功。
 * @param storeView              Store 读侧视图。
 * @param compiledModel          已编译授权模型。
 * @param preflightFailureReason 前置检查失败原因；为空表示前置检查通过。
 */
record PermissionEvaluationContext(PermissionCheckStatus abnormalStatus, StoreView storeView,
                                   CompiledAuthorizationModel compiledModel,
                                   EvaluationExplainReason preflightFailureReason) {

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
     * @param storeView              Store 读侧视图
     * @param compiledModel          已编译授权模型
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
}
