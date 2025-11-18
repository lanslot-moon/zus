package org.kitona.zus.api.entity.request;

import lombok.Builder;
import lombok.Data;

/**
 * Watch API请求对象
 */
@Data
@Builder
public class WatchApiRequest {
    private String namespace;
    private Long startVersion; // 起始版本号
    private Long endVersion;   // 结束版本号，可为空（表示实时监控）
    private Long requestId;
    private Integer channelBufferSize; // 通道缓冲区大小
}