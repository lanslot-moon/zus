package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.query.StoreView;
import org.kitona.zus.domain.valueobject.CursorPageResult;

import java.util.Optional;

/**
 * Store 查询仓储接口（读侧）
 *
 * <p>用于返回面向展示的查询视图，不承担聚合装载与保存职责。
 */
public interface IStoreQueryRepository {

    /**
     * 查询单个 Store 读侧视图
     *
     * @param storeId 存储空间ID
     * @return 读侧视图，不存在返回 empty
     */
    Optional<StoreView> findViewByStoreId(String storeId);

    /**
     * 游标分页查询 Store 读侧视图
     *
     * @param pageToken 分页游标
     * @param pageSize  每页大小
     * @return 分页结果
     */
    CursorPageResult<StoreView> findPageViewByCursor(String pageToken, int pageSize);
}
