package org.kitona.zus.api.response;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 通用游标分页响应 VO（不可变）
 *
 * <p>用于封装 API 层的游标分页查询响应，与 Service 层的 {@code PageResultDTO} 对应。
 *
 * @param <T> 数据元素类型
 */
@Getter
public class PageResponseVO<T> {

    /**
     * 当前页数据列表
     */
    private final List<T> data;

    /**
     * 下一页游标，为 null 表示没有更多数据
     */
    private final String continuationToken;

    /**
     * 是否有更多数据
     */
    private final boolean hasMore;

    private PageResponseVO(List<T> data, String continuationToken, boolean hasMore) {
        this.data = data != null ? data : Collections.emptyList();
        this.continuationToken = continuationToken;
        this.hasMore = hasMore;
    }

    public static <T> PageResponseVO<T> empty() {
        return new PageResponseVO<>(Collections.emptyList(), null, false);
    }

    public static <T> PageResponseVO<T> of(List<T> data, String continuationToken) {
        boolean hasMore = continuationToken != null && !continuationToken.isEmpty();
        return new PageResponseVO<>(data, continuationToken, hasMore);
    }

    public static <T> PageResponseVO<T> of(List<T> data, String continuationToken, boolean hasMore) {
        return new PageResponseVO<>(data, continuationToken, hasMore);
    }
}
