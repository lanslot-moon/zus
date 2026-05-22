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
     * @param trace trace 参数
     * @return 返回结果
     */
    public static PermissionExplainOutcome allowed(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.ALLOWED, trace);
    }

    /**
     * 创建拒绝状态的授权解释结果。
     *
     * @param trace trace 参数
     * @return 返回结果
     */
    public static PermissionExplainOutcome denied(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.DENIED, trace);
    }

    /**
     * 创建 Store 不存在的授权解释结果。
     * @return 返回结果
     */
    public static PermissionExplainOutcome storeNotFound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.STORE_NOT_FOUND, null);
    }

    /**
     * 创建模型未绑定的授权解释结果。
     * @return 返回结果
     */
    public static PermissionExplainOutcome modelNotBound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_NOT_BOUND, null);
    }

    /**
     * 创建模型不存在的授权解释结果。
     * @return 返回结果
     */
    public static PermissionExplainOutcome modelNotFound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_NOT_FOUND, null);
    }

    /**
     * 创建模型无效的授权解释结果。
     * @return 返回结果
     */
    public static PermissionExplainOutcome modelInvalid() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_INVALID, null);
    }

    /**
     * 判断is allowed。
     * @return 满足条件返回 true，否则返回 false
     */
    public boolean isAllowed() {
        return PermissionCheckStatus.ALLOWED == status;
    }

    /**
     * 判断is denied。
     * @return 满足条件返回 true，否则返回 false
     */
    public boolean isDenied() {
        return PermissionCheckStatus.DENIED == status;
    }
}
