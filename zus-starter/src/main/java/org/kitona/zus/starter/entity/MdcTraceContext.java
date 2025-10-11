package org.kitona.zus.starter.entity;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

public class MdcTraceContext {

    private MdcTraceContext() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *  日志文件中全链路标识的占位符
     */
    public static final String TRACE_ID_PATTERN = "traceId";

    /**
     * 请求头的链路Key
     */
    public static final String TRACE_HEADER_KEY = "X-Requested-Id";


    /**
     * 初始化TraceId
     */
    public static void initTraceId(String traceId) {
        traceId = StringUtils.isBlank(traceId) ? UUID.randomUUID().toString() : traceId;
        MDC.put(TRACE_ID_PATTERN, traceId);
    }

    /**
     * 获取当前TraceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_PATTERN);
    }

    /**
     * 清除TraceId
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_PATTERN);
    }

    /**
     * 清除底层数据
     */
    public static void clear() {
        MDC.clear();
    }
}
