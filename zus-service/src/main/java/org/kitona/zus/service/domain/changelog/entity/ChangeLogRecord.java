package org.kitona.zus.service.domain.changelog.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 变更日志条目
 */
@Data
@Builder
public class ChangeLogRecord {
    private Long version;                      // 全局递增版本号
    private String namespace;                  // 命名空间
    private String operation;                  // 操作类型: add, remove, conditional_write
    private String object;                     // 对象（如 "document:123"）
    private String relation;                   // 关系（如 "writer"）
    private String user;                       // 用户（如 "user:alice"）
    private List<String> users;                // 批量用户（批量操作）
    private LocalDateTime timestamp;           // 时间戳
    private String condition;                  // 条件（如果有）
    private String traceId;                    // 追踪ID
    private String metadata;                   // 附加元数据
    private boolean isSystemChange;           // 是否为系统变更
    private long checksum;                     // 数据校验和
}
    