package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * 监听统计响应
 */
@Data
@Builder
public class WatchStatsResponse {
    private Integer activeSessions;       // 活跃会话数
    private Long processedChanges;        // 已处理的变更数
    private Long lastChangeTime;          // 最后变更时间
    private Long averageLatency;          // 平均延迟（毫秒）
    private String status;                // 监听服务状态
    private Long timestamp;               // 统计时间戳
}
