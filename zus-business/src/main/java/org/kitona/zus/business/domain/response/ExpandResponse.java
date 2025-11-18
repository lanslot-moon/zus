package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * Expand API响应对象
 */
@Data
@Builder
public class ExpandResponse {
    private String object;             // 展开的对象
    private String relation;           // 展开的关系
    private UserSet tree;              // 用户集合树
    private Long requestId;
    private Long version;              // 版本号
    private Long timestamp;            // 时间戳
}