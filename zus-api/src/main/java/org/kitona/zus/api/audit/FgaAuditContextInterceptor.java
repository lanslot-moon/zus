package org.kitona.zus.api.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 从 HTTP Header 解析 FGA 审计元数据并写入 {@link FgaAuditContext}
 *
 * <h3>约定的 Header</h3>
 * <ul>
 *   <li>{@code X-Fga-Operator-Id}：操作人标识</li>
 *   <li>{@code X-Fga-Request-Id}：请求追踪 ID（缺失时退化为 {@code X-Request-Id} 或 {@code traceId} MDC）</li>
 *   <li>{@code X-Fga-Source}：操作来源，默认 {@code API}</li>
 * </ul>
 *
 * <p>通过 {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer} 注册到所有
 * {@code /fga/**} 路径。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Component
public class FgaAuditContextInterceptor implements HandlerInterceptor {

    public static final String HEADER_OPERATOR_ID = "X-Fga-Operator-Id";
    public static final String HEADER_REQUEST_ID = "X-Fga-Request-Id";
    public static final String HEADER_SOURCE = "X-Fga-Source";
    public static final String FALLBACK_REQUEST_ID = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String operatorId = request.getHeader(HEADER_OPERATOR_ID);
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = request.getHeader(FALLBACK_REQUEST_ID);
        }
        String source = request.getHeader(HEADER_SOURCE);
        if (source == null || source.isBlank()) {
            source = "API";
        }

        FgaAuditContext.set(FgaAuditContext.builder()
                .operatorId(operatorId)
                .requestId(requestId)
                .source(source)
                .build());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        FgaAuditContext.clear();
    }
}
