package org.kitona.zus.domain.valueobject;

/**
 * 鉴权结果值对象
 *
 * <p>用于表达一次鉴权用例的最终语义结果。
 */
public record PermissionCheckResult(PermissionCheckStatus status) {

    /**
     * 创建允许结果。
     *
     * @return 权限检查结果
     */
    public static PermissionCheckResult allowed() {
        return new PermissionCheckResult(PermissionCheckStatus.ALLOWED);
    }

    /**
     * 创建拒绝结果。
     *
     * @return 权限检查结果
     */
    public static PermissionCheckResult denied() {
        return new PermissionCheckResult(PermissionCheckStatus.DENIED);
    }

    /**
     * 创建上下文准备失败结果。
     *
     * <p>Store 不存在、模型未绑定、模型不存在、模型无效这类状态已经由
     * {@link PermissionCheckStatus} 明确表达，调用方不需要再通过分支把状态映射成结果对象。
     *
     * @param status 上下文准备失败状态
     * @return 权限检查结果
     */
    public static PermissionCheckResult abnormal(PermissionCheckStatus status) {
        if (PermissionCheckStatus.ALLOWED == status || PermissionCheckStatus.DENIED == status) {
            throw new IllegalArgumentException("Check abnormal status must not be authorization decision: " + status);
        }
        return new PermissionCheckResult(status);
    }


    /**
     * 判断是否允许。
     *
     * @return 允许返回 {@code true}
     */
    public boolean isAllowed() {
        return PermissionCheckStatus.ALLOWED == status;
    }

    /**
     * 判断是否拒绝。
     *
     * @return 拒绝返回 {@code true}
     */
    public boolean isDenied() {
        return PermissionCheckStatus.DENIED == status;
    }
}
