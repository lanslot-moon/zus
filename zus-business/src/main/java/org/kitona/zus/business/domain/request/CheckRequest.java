package org.kitona.zus.business.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Check API请求对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckRequest {
    private String namespace;
    private String object;    // 如 "resource:123"
    private String relation;  // 如 "writer"
    private String user;      // 如 "user:alice"
    private Long deadline;    // 请求超时时间
    private String traceId;   // 追踪ID
}