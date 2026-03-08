package org.kitona.zus.domain.valueobject;

import java.util.Collections;
import java.util.List;

/**
 * 游标分页结果（值对象）
 *
 * <p>用于封装游标分页查询的结果，包含：
 * <ul>
 *   <li>data: 当前页数据列表</li>
 *   <li>nextPageToken: 下一页游标，为 null 表示没有更多数据</li>
 *   <li>hasMore: 是否有更多数据</li>
 * </ul>
 *
 * <p>游标分页相比偏移分页的优势：
 * <ul>
 *   <li>性能更好：避免 OFFSET 导致的深分页性能问题</li>
 *   <li>数据一致性：不会因为数据插入/删除导致重复或遗漏</li>
 * </ul>
 *
 * @param <T> 数据元素类型
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-13
 */
public record CursorPageResult<T>(
        List<T> data,
        String nextPageToken,
        boolean hasMore
) {

    /**
     * 规范化构造函数，确保 data 不为 null
     */
    public CursorPageResult {
        if (data == null) {
            data = Collections.emptyList();
        }
    }

    /**
     * 创建空结果
     *
     * @param <T> 数据元素类型
     * @return 空的分页结果
     */
    public static <T> CursorPageResult<T> empty() {
        return new CursorPageResult<>(Collections.emptyList(), null, false);
    }

    /**
     * 创建分页结果
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
     * 创建分页结果（根据 pageSize 自动判断是否有更多）
     *
     * <p>如果返回数据量等于 pageSize，说明可能还有更多数据，
     * 使用最后一条记录的标识作为下一页游标。
     *
     * @param data            当前页数据
     * @param pageSize        请求的页大小
     * @param lastItemCursor  最后一条记录的游标值（通常是 ID 或时间戳）
     * @param <T>             数据元素类型
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
     * 获取数据条数
     *
     * @return 数据条数
     */
    public int size() {
        return data.size();
    }

    /**
     * 判断是否为空
     *
     * @return 空返回 true
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
