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

    public static PermissionExplainOutcome allowed(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.ALLOWED, trace);
    }

    public static PermissionExplainOutcome denied(EvaluationTrace trace) {
        return new PermissionExplainOutcome(PermissionCheckStatus.DENIED, trace);
    }

    public static PermissionExplainOutcome storeNotFound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.STORE_NOT_FOUND, null);
    }

    public static PermissionExplainOutcome modelNotBound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_NOT_BOUND, null);
    }

    public static PermissionExplainOutcome modelNotFound() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_NOT_FOUND, null);
    }

    public static PermissionExplainOutcome modelInvalid() {
        return new PermissionExplainOutcome(PermissionCheckStatus.MODEL_INVALID, null);
    }

    public boolean isAllowed() {
        return PermissionCheckStatus.ALLOWED == status;
    }

    public boolean isDenied() {
        return PermissionCheckStatus.DENIED == status;
    }
}
