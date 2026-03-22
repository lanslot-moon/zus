package org.kitona.zus.domain.valueobject;

import java.util.Objects;

/**
 * 鉴权结果值对象
 *
 * <p>用于表达一次鉴权用例的最终语义结果。
 */
public record AuthorizationCheckResult(AuthorizationCheckStatus status) {

    public AuthorizationCheckResult {
        status = Objects.requireNonNull(status, "status 不能为空");
    }

    public static AuthorizationCheckResult allowed() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.ALLOWED);
    }

    public static AuthorizationCheckResult denied() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.DENIED);
    }

    public static AuthorizationCheckResult storeNotFound() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.STORE_NOT_FOUND);
    }

    public static AuthorizationCheckResult modelNotBound() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.MODEL_NOT_BOUND);
    }

    public static AuthorizationCheckResult modelNotFound() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.MODEL_NOT_FOUND);
    }

    public static AuthorizationCheckResult modelInvalid() {
        return new AuthorizationCheckResult(AuthorizationCheckStatus.MODEL_INVALID);
    }

    public boolean isAllowed() {
        return AuthorizationCheckStatus.ALLOWED == status;
    }

    public boolean isDenied() {
        return AuthorizationCheckStatus.DENIED == status;
    }

    public boolean isAbnormal() {
        return !isAllowed() && !isDenied();
    }
}
