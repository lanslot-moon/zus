package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;

import java.util.List;

/**
 * Watch 应用服务接口（历史变更查询，供 SSE 发送或分页拉取）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleWatchApplicationService {

    /**
     * 获取 startAt（不含）之后的变更，最多 limit 条。
     * <p>主要供 SSE 推送使用。
     */
    List<TupleChangeResultDTO> getChanges(String storeId, Long startAt, int limit);

    /**
     * 获取当前最大 Zookie
     */
    long getCurrentZookie(String storeId);

    /**
     * 基于 zookie 的游标分页变更查询。
     *
     * <p>游标语义：{@code continuationToken} 为上一次返回中的最后一条记录的 zookie。
     * 传入 {@code null} 表示从最早（zookie=0 之后）开始。
     * 返回结果里的 {@code continuationToken} 为本批最后一条的 zookie，没有更多数据时为 {@code null}。
     *
     * @param storeId            存储空间 ID
     * @param continuationToken  上一次返回的游标（zookie 字符串），首次查询传 null
     * @param pageSize           页大小，非正数使用默认值
     * @param objectTypeFilter   可选的对象类型过滤（在应用层做内存过滤）
     * @return 变更日志分页结果
     */
    PageResultDTO<TupleChangeResultDTO> listChanges(String storeId, String continuationToken,
                                                    int pageSize, String objectTypeFilter);
}
