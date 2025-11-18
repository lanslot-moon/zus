package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Check API响应对象
 */
@Data
@Builder
public class CheckResponse {
    private boolean allowed;           // 是否允许访问
    private List<String> trace;        // 检查路径追踪
    private Long requestId;
    private Long deadline;
    private String traceId;
    private Map<String, Object> metadata; // 元数据
}