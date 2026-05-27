package org.kitona.zus.service.application.coordinator;

import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;

/**
 * Explain 编排结果。
 *
 * @param status 鉴权语义状态
 * @param trace  explain 解释树，异常状态下为空
 */
public record PermissionExplainOutcome(PermissionCheckStatus status, EvaluationTrace trace) {

    /**
     * 创建允许状态的授权解释结果。
     *
     * @param trace explain 解释树
     * @return 允许状态的授权解释结果
     */
    public static PermissionExplainOutcome allowed(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.ALLOWED, trace);
    }

    /**
     * 创建拒绝状态的授权解释结果。
     *
     * @param trace explain 解释树
     * @return 拒绝状态的授权解释结果
     */
    public static PermissionExplainOutcome denied(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.DENIED, trace);
    }

    /**
     * 创建上下文准备失败的授权解释结果。
     *
     * <p>Store 不存在、模型未绑定、模型不存在、模型无效这类状态已经由
     * {@link PermissionCheckStatus} 明确表达，Explain 结果对象不需要为每个状态
     * 再暴露一组重复工厂方法，避免调用方出现状态转换分支。
     *
     * @param status 上下文准备失败状态
     * @return 授权解释结果
     */
    public static PermissionExplainOutcome abnormal(PermissionCheckStatus status) {
        if (PermissionCheckStatus.ALLOWED == status || PermissionCheckStatus.DENIED == status) {
            throw new IllegalArgumentException("Explain abnormal status must not be authorization decision: " + status);
        }
        return new PermissionExplainOutcome(status, null);
    }

    /**
     * 判断是否为允许状态。
     *
     * @return 允许状态返回 {@code true}
     */
    public boolean isAllowed() {
        return PermissionCheckStatus.ALLOWED == status;
    }

    /**
     * 判断是否为拒绝状态。
     *
     * @return 拒绝状态返回 {@code true}
     */
    public boolean isDenied() {
        return PermissionCheckStatus.DENIED == status;
    }
}
