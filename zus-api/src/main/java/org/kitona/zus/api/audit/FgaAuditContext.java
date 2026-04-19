package org.kitona.zus.api.audit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FGA 审计上下文 —— 绑定在线程本地的审计元数据
 *
 * <p>所有写操作的审计字段不再从请求体读取，改为由 HTTP Header 注入，由
 * {@link FgaAuditContextInterceptor} 在请求进入 Controller 前填充，
 * 供下游应用服务写入 {@code fga_tuple_changelog} 时使用。
 *
 * <p>对应数据库表 {@code fga_tuple_changelog} 字段：
 * <ul>
 *   <li>{@link #operatorId} → {@code operator_id}</li>
 *   <li>{@link #requestId}  → {@code request_id}</li>
 *   <li>{@link #source}     → {@code source}</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class FgaAuditContext {

    /**
     * 操作人标识
     */
    private String operatorId;

    /**
     * 请求追踪 ID（可退化为 TraceId）
     */
    private String requestId;

    /**
     * 操作来源：{@code API} / {@code SYNC} / {@code CLEANUP} / {@code MIGRATION}
     */
    private String source;

    // ========== ThreadLocal Holder ==========

    private static final ThreadLocal<FgaAuditContext> HOLDER = new ThreadLocal<>();

    public static void set(FgaAuditContext context) {
        HOLDER.set(context);
    }

    public static FgaAuditContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 空安全获取；未绑定时返回一个默认的 API 来源上下文
     */
    public static FgaAuditContext current() {
        FgaAuditContext context = HOLDER.get();
        return context != null ? context : FgaAuditContext.builder().source("API").build();
    }
}
