package org.kitona.zus.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 鉴权结果状态
 *
 * <p>明确区分真正的权限拒绝与系统/配置异常，避免将所有失败都压扁为 false。
 */
@AllArgsConstructor
@Getter
public enum PermissionCheckStatus {
    ALLOWED(null),
    DENIED(null),
    STORE_NOT_FOUND("store 不存在"),
    MODEL_NOT_BOUND("store 未绑定授权模型"),
    MODEL_NOT_FOUND("当前授权模型不存在"),
    MODEL_INVALID("当前授权模型无效");

    private final String desc;
}
