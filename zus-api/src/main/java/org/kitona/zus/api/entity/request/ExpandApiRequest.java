package org.kitona.zus.api.entity.request;

import lombok.Builder;
import lombok.Data;

/**
 * Expand API请求对象
 */
@Data
@Builder
public class ExpandApiRequest {
    private String namespace;
    private String object;    // 要展开的对象和关系
    private String relation;
    private Long requestId;
    private Long version;     // 版本号，用于一致性读取
}