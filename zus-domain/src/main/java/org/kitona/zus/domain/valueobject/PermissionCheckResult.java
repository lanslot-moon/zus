package org.kitona.zus.domain.valueobject;

/**
 * 鉴权结果值对象
 *
 * <p>用于表达一次鉴权用例的最终语义结果。
 */
public record PermissionCheckResult(PermissionCheckStatus status) {

    public static PermissionCheckResult allowed() {
        return new PermissionCheckResult(PermissionCheckStatus.ALLOWED);
    }

    public static PermissionCheckResult denied() {
        return new PermissionCheckResult(PermissionCheckStatus.DENIED);
    }

    public static PermissionCheckResult storeNotFound() {
        return new PermissionCheckResult(PermissionCheckStatus.STORE_NOT_FOUND);
    }

    public static PermissionCheckResult modelNotBound() {
        return new PermissionCheckResult(PermissionCheckStatus.MODEL_NOT_BOUND);
    }

    public static PermissionCheckResult modelNotFound() {
        return new PermissionCheckResult(PermissionCheckStatus.MODEL_NOT_FOUND);
    }

    public static PermissionCheckResult modelInvalid() {
        return new PermissionCheckResult(PermissionCheckStatus.MODEL_INVALID);
    }

    public boolean isAllowed() {
        return PermissionCheckStatus.ALLOWED == status;
    }

    public boolean isDenied() {
        return PermissionCheckStatus.DENIED == status;
    }

    public boolean isAbnormal() {
        return !isAllowed() && !isDenied();
    }
}
