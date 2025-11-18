package org.kitona.zus.api.entity.request;

import lombok.Builder;
import lombok.Data;
import org.kitona.zus.business.domain.response.WatchResponse;

import java.util.List;

/**
 * 批量轮询响应
 */
@Data
@Builder
public class ChangePollApiResponse {
    private List<WatchResponse> changes;  // 变更列表
    private Long nextVersion;             // 下次轮询的起始版本
    private Integer totalChanges;         // 总变更数
    private Long timestamp;               // 响应时间戳
}