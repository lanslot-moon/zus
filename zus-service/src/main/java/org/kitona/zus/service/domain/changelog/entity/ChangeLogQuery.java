package org.kitona.zus.service.domain.changelog.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 变更日志查询参数
 */
@Data
@Builder
public class ChangeLogQuery {
    private String namespace;                  // 命名空间过滤
    private Long fromVersion;                  // 起始版本
    private Long toVersion;                    // 结束版本
    private LocalDateTime fromTime;            // 起始时间
    private LocalDateTime toTime;              // 结束时间
    private List<String> operations;           // 操作类型过滤
    private List<String> objects;              // 对象过滤
    private String relation;                   // 关系过滤
    private String user;                       // 用户过滤
    private int limit;                         // 限制数量
    private String traceId;                    // 追踪ID过滤
    private boolean includeMetadata;           // 是否包含元数据
}
    