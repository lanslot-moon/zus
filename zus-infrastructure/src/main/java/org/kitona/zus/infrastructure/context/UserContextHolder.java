package org.kitona.zus.infrastructure.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用于在API层设置并管理当前请求用户上下文信息。
 * 支持 try-with-resources 自动清理。
 * <p>
 * 使用示例：
 * try (UserContextHolder ctx = UserContextHolder.with(userId, userName, tenantId)) {
 * // 在这里的任意代码中都可以通过 UserContextHolder.getUserId() 获取用户ID
 * }
 */
public class UserContextHolder implements AutoCloseable {


    private static final TransmittableThreadLocal<UserInfo> USER_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * 设置用户上下文并返回 AutoCloseable 实例，支持 try-with-resources 自动清理。
     */
    public static UserContextHolder with(String userId, String tenantId) {
        UserInfo info = new UserInfo(userId, tenantId);
        USER_CONTEXT.set(info);
        return new UserContextHolder();
    }

    /**
     * 获取当前线程的用户信息
     */
    public static UserInfo get() {
        return USER_CONTEXT.get();
    }

    public static String getUserId() {
        UserInfo info = USER_CONTEXT.get();
        return info != null ? info.userId() : null;
    }


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

    /**
     * try-with-resources 自动清理
     */
    @Override
    public void close() {
        USER_CONTEXT.remove();
    }

    /**
     * 用户信息封装类
     */
    public record UserInfo(String userId, String tenantId) implements Serializable {
        @Serial
        private static final long serialVersionUID = 9117057030245686716L;
    }
}
