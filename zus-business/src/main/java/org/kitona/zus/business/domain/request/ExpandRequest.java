package org.kitona.zus.business.domain.request;

import lombok.Builder;
import lombok.Data;

/**
 * Expand API请求对象
 */
@Data
@Builder
public class ExpandRequest {
    private String namespace;
    private String object;    // 要展开的对象和关系
    private String relation;
    private Long requestId;
    private Long version;     // 版本号，用于一致性读取
}