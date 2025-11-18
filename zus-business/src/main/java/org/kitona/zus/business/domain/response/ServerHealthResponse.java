package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 健康检查响应
 */
@Data
@Builder
public class ServerHealthResponse {
    private String serverId;
    private String namespace;
    private String status;             // healthy, degraded, unhealthy
    private Map<String, Long> stats;   // 服务器统计信息
    private Long timestamp;
    private String version;            // 服务器版本
}