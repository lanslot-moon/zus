package org.kitona.zus.starter.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.kitona.zus.common.context.MdcTraceContext;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用于设置Rest和OpenFeign请求链路追踪的Tid
 * 
 * @link <a href="https://blog.liushigong.cn/post/25"/a>
 */
@Slf4j
public class MdcHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 日志处理
        String traceId = request.getHeader(MdcTraceContext.TRACE_HEADER_KEY);
        traceId = StringUtils.isBlank(MdcTraceContext.getTraceId()) ? traceId : MdcTraceContext.getTraceId();
        MdcTraceContext.initTraceId(traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MdcTraceContext.clearTraceId();
    }
}
