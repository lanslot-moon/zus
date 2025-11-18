package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * Watch Server配置信息
 */
@Data
@Builder
public class WatchServerConfig {
    private Integer defaultBufferSize;    // 默认缓冲区大小
    private Long sessionTimeout;          // 会话超时时间（毫秒）
    private Integer maxConcurrentSessions; // 最大并发会话数
    private String storageBackend;        // 存储后端类型
    private Boolean enableWebSocket;      // 是否启用WebSocket
    private Boolean enableSse;            // 是否启用SSE
    private Integer heartbeatInterval;    // 心跳间隔（秒）
}