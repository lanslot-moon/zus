package org.kitona.zus.domain.read.page;

import java.util.Collections;
import java.util.List;

/**
 * 读侧游标分页结果。
 *
 * <p>该对象只服务于 CQRS 读侧查询，用于封装列表页数据、下一页游标以及是否还有更多数据。
 * 它不属于授权核心领域值对象，避免把分页展示语义混入 subject、object、tuple、zookie 等核心模型。
 *
 * @param data          当前页数据列表
 * @param nextPageToken 下一页游标，为 null 表示没有更多数据
 * @param hasMore       是否还有更多数据
 * @param <T>           数据元素类型
 */
public record CursorPageResult<T>(
        List<T> data,
        String nextPageToken,
        boolean hasMore
) {

    /**
     * 规范化构造函数，确保 data 不为 null。
     */
    public CursorPageResult {
        if (data == null) {
            data = Collections.emptyList();
        }
    }

    /**
     * 创建空分页结果。
     *
     * @param <T> 数据元素类型
     * @return 空分页结果
     */
    public static <T> CursorPageResult<T> empty() {
        return new CursorPageResult<>(Collections.emptyList(), null, false);
    }

    /**
     * 根据下一页游标创建分页结果。
     *
     * @param data          当前页数据
     * @param nextPageToken 下一页游标
     * @param <T>           数据元素类型
     * @return 分页结果
     */
    public static <T> CursorPageResult<T> of(List<T> data, String nextPageToken) {
        boolean hasMore = nextPageToken != null && !nextPageToken.isEmpty();
        return new CursorPageResult<>(data, nextPageToken, hasMore);
    }

    /**
     * 根据页大小和最后一条记录游标创建分页结果。
     *
     * <p>游标分页避免深分页 OFFSET 带来的性能问题，也能降低数据插入或删除导致的重复、遗漏风险。
     *
     * @param data           当前页数据
     * @param pageSize       请求页大小
     * @param lastItemCursor 最后一条记录的游标值
     * @param <T>            数据元素类型
     * @return 分页结果
     */
    public static <T> CursorPageResult<T> of(List<T> data, int pageSize, String lastItemCursor) {
        if (data == null || data.isEmpty()) {
            return empty();
        }
        boolean hasMore = data.size() >= pageSize;
        String nextToken = hasMore ? lastItemCursor : null;
        return new CursorPageResult<>(data, nextToken, hasMore);
    }

    /**
     * 获取当前页数据条数。
     *
     * @return 数据条数
     */
    public int size() {
        return data.size();
    }

    /**
     * 判断当前分页结果是否为空。
     *
     * @return 为空返回 true
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
