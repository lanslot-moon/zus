package org.kitona.zus.service.domain.tuple.entity;


import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 版本查询参数
 */
@Data
@Builder
public class VersionQuery {
    private String namespace;              // 命名空间
    private Long fromVersion;              // 起始版本
    private Long toVersion;                // 结束版本
    private Long fromTime;        // 起始时间
    private Long toTime;          // 结束时间
    private List<String> operations;       // 操作类型过滤
    private int limit;                     // 限制数量
}