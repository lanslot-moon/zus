package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * Watch API响应对象
 */
@Data
@Builder
public class WatchResponse {
    private Long version;              // 变更版本号
    private String operation;          // 操作类型: "add" 或 "remove"
    private String object;             // 变更的对象
    private String relation;           // 变更的关系
    private String user;               // 变更的用户
    private Long timestamp;            // 变更时间戳
}
