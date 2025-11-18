package org.kitona.zus.service.domain.changelog.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 变更日志统计信息
 */
@Data
@Builder
public class ChangeLogStats {
    private long totalEntries;                 // 总条目数
    private long currentVersion;               // 当前版本
    private String oldestEntryTime;            // 最旧条目时间
    private String newestEntryTime;            // 最新条目时间
    private long namespaceCount;               // 命名空间数量
    private Map<String, Long> operationStats;  // 操作类型统计
    private Map<String, Long> namespaceStats;  // 命名空间统计
    private long storageSize;                  // 存储大小（字节）
    private long averageEntrySize;             // 平均条目大小
}