package org.kitona.zus.service.domain.tuple.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 元组查询参数
 */
@Data
@Builder
public class TupleQuery {
    private String namespace;              // 命名空间
    private String object;                 // 对象过滤
    private String relation;               // 关系过滤
    private String user;                   // 用户过滤
    private Boolean active;                // 激活状态过滤
    private Long fromTime;        // 起始时间
    private Long toTime;          // 结束时间
    private List<String> objects;          // 批量对象过滤
    private List<String> relations;        // 批量关系过滤
    private List<String> users;            // 批量用户过滤
    private int limit;                     // 限制数量
    private boolean includeHistory;        // 是否包含历史记录
    private String condition;              // 条件过滤
}
    