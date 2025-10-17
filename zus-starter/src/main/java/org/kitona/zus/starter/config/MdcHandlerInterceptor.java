package org.kitona.zus.starter.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.starter.entity.MdcTraceContext;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;


/**
 * 用于设置Rest和OpenFeign请求链路追踪的Tid
 * @link <a href="https://blog.liushigong.cn/post/25"/a>
 */
@Slf4j
public class MdcHandlerInterceptor implements HandlerInterceptor, RequestInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 日志处理
        String traceId = request.getHeader(MdcTraceContext.TRACE_HEADER_KEY);
        traceId = StringUtils.isBlank(MdcTraceContext.getTraceId()) ? traceId : MdcTraceContext.getTraceId();
        MdcTraceContext.initTraceId(traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MdcTraceContext.clearTraceId();
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (requestTemplate == null) {
            log.warn("MdcHandlerInterceptor apply requestTemplate is null");
            return;
        }
        String traceId = MdcTraceContext.getTraceId();
        traceId = StringUtils.isBlank(traceId) ? UUID.randomUUID().toString() : traceId;

        requestTemplate.header(MdcTraceContext.TRACE_HEADER_KEY, traceId);
    }
}
