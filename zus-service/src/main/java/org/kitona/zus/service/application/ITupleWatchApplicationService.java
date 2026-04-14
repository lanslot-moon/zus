package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.response.TupleChangeResultDTO;

import java.util.List;

/**
 * Watch 应用服务接口（历史变更查询，供 SSE 发送）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleWatchApplicationService {

    /**
     * 获取 startAt（不含）之后的变更，最多 limit 条
     */
    List<TupleChangeResultDTO> getChanges(String storeId, Long startAt, int limit);

    /**
     * 获取当前最大 Zookie
     */
    long getCurrentZookie(String storeId);
}
