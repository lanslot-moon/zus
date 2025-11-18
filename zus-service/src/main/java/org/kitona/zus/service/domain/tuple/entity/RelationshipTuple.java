package org.kitona.zus.service.domain.tuple.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 关系元组
 */
@Data
@Builder
public class RelationshipTuple {
    private String namespace;              // 命名空间
    private String object;                 // 对象（如 "document:123"）
    private String relation;               // 关系（如 "writer"）
    private String user;                   // 用户（如 "user:alice"）
    private Long createdAt;       // 创建时间
    private Long updatedAt;       // 更新时间
    private String condition;              // 条件（如果有）
    private String traceId;                // 追踪ID
    private String metadata;               // 附加元数据
    private boolean active;                // 是否激活
    private long version;                  // 版本号
}