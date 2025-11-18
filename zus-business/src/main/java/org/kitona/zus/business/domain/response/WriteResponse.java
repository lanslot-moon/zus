package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

/**
 * Write API响应对象
 */
@Data
@Builder
public class WriteResponse {
    private Long requestId;
    private Long version;              // 写入后的版本号
    private Integer writesCount;       // 成功写入的元组数量
    private Integer deletesCount;      // 成功删除的元组数量
    private String traceId;
}