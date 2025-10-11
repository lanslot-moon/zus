package org.kitona.zus.starter.config;

import org.kitona.zus.starter.entity.MdcTraceContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于设置Rest请求链路追踪的Tid
 * @link <a href="https://blog.liushigong.cn/post/25"/a>
 */
public class MdcHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 日志处理
        String traceId = request.getHeader(MdcTraceContext.TRACE_HEADER_KEY);
        MdcTraceContext.initTraceId(traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MdcTraceContext.clearTraceId();
    }
}
