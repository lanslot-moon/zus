package org.kitona.zus.domain.valueobject;

/**
 * 鉴权结果状态
 *
 * <p>明确区分真正的权限拒绝与系统/配置异常，避免将所有失败都压扁为 false。
 */
public enum PermissionCheckStatus {
    ALLOWED,
    DENIED,
    STORE_NOT_FOUND,
    MODEL_NOT_BOUND,
    MODEL_NOT_FOUND,
    MODEL_INVALID
}
