package org.kitona.zus.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户上下文持有器
 *
 * <p>用于在请求链路中传递当前用户信息，支持跨线程传递（基于 TTL）。
 * <p>支持 try-with-resources 自动清理。
 *
 * <p>使用示例：
 * <pre>{@code
 * try (UserContextHolder ctx = UserContextHolder.with(userId, tenantId)) {
 *     // 在这里的任意代码中都可以通过 UserContextHolder.getUserId() 获取用户ID
 * }
 * }</pre>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public class UserContextHolder implements AutoCloseable {

    private static final TransmittableThreadLocal<UserInfo> USER_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * 设置用户上下文并返回 AutoCloseable 实例，支持 try-with-resources 自动清理。
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return UserContextHolder 实例
     */
    public static UserContextHolder with(String userId, String tenantId) {
        UserInfo info = new UserInfo(userId, tenantId);
        USER_CONTEXT.set(info);
        return new UserContextHolder();
    }

    /**
     * 获取当前线程的用户信息
     *
     * @return 用户信息，可能为 null
     */
    public static UserInfo get() {
        return USER_CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID，未设置时返回 null
     */
    public static String getUserId() {
        UserInfo info = USER_CONTEXT.get();
        return info != null ? info.userId() : null;
    }

    /**
     * 获取当前租户ID
     *
     * @return 租户ID，未设置时返回 null
     */
    public static String getTenantId() {
        UserInfo info = USER_CONTEXT.get();
        return info != null ? info.tenantId() : null;
    }

    /**
     * 手动清除上下文
     */
    public static void clear() {
        USER_CONTEXT.remove();
    }

    @Override
    public void close() {
        USER_CONTEXT.remove();
    }

    /**
     * 用户信息封装类
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     */
    public record UserInfo(String userId, String tenantId) implements Serializable {
        @Serial
        private static final long serialVersionUID = 9117057030245686716L;
    }
}
