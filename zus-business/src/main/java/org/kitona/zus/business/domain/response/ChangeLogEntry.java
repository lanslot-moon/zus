package org.kitona.zus.business.domain.response;
import lombok.Builder;
import lombok.Data;

/**
 * 变更日志记录
 */
@Data
@Builder
public class ChangeLogEntry {
    private Long version;              // 全局递增版本号
    private String namespace;          // 命名空间
    private String operation;          // add, remove, conditional_write
    private String object;             // 对象
    private String relation;           // 关系
    private String user;               // 用户
    private String[] users;            // 批量用户（批量操作）
    private Long timestamp;            // 时间戳
    private String condition;          // 条件（如果有）
    private String traceId;            // 追踪ID
}