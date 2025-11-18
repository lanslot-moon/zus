package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * 心跳响应
 */
@Data
@Builder
public class HeartbeatResponse {
    private boolean alive;                // 连接是否存活
    private Long timestamp;               // 时间戳
    private String message;               // 附加信息
}