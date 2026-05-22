package org.kitona.zus.service.dto.response;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用游标分页结果 DTO（不可变）
 *
 * <p>用于封装应用服务层的游标分页查询结果，与领域读侧的 {@code CursorPageResult} 对应。
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>不可变对象：无 setter，通过工厂方法创建</li>
 *   <li>应用服务层使用此 DTO 作为分页查询的统一返回结构</li>
 *   <li>与领域读侧的 {@code CursorPageResult} 结构一致，便于转换</li>
 *   <li>泛型设计支持不同业务 DTO 的分页返回</li>
 * </ul>
 *
 * @param <T> 数据元素类型
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
@Getter
public class PageResultDTO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    /**
     * 私有构造函数，通过工厂方法创建实例
     */
    private PageResultDTO(List<T> data, String continuationToken, boolean hasMore) {
        this.data = data != null ? data : Collections.emptyList();
        this.continuationToken = continuationToken;
        this.hasMore = hasMore;
    }

    /**
     * 创建空结果
     *
     * @param <T> 数据元素类型
     * @return 空的分页结果
     */
    public static <T> PageResultDTO<T> empty() {
        return new PageResultDTO<>(Collections.emptyList(), null, false);
    }

    /**
     * 创建分页结果
     *
     * @param data              当前页数据
     * @param continuationToken 下一页游标
     * @param <T>               数据元素类型
     * @return 分页结果
     */
    public static <T> PageResultDTO<T> of(List<T> data, String continuationToken) {
        boolean hasMore = continuationToken != null && !continuationToken.isEmpty();
        return new PageResultDTO<>(data, continuationToken, hasMore);
    }

    /**
     * 创建分页结果（根据 pageSize 自动判断是否有更多）
     *
     * @param data           当前页数据
     * @param pageSize       请求的页大小
     * @param lastItemCursor 最后一条记录的游标值
     * @param <T>            数据元素类型
     * @return 分页结果
     */
    public static <T> PageResultDTO<T> of(List<T> data, int pageSize, String lastItemCursor) {
        if (data == null || data.isEmpty()) {
            return empty();
        }
        boolean hasMore = data.size() >= pageSize;
        String nextToken = hasMore ? lastItemCursor : null;
        return new PageResultDTO<>(data, nextToken, hasMore);
    }

    /**
     * 获取数据条数
     *
     * @return 数据条数
     */
    public int size() {
        return data != null ? data.size() : 0;
    }

    /**
     * 判断是否为空
     *
     * @return 空返回 true
     */
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }
}
