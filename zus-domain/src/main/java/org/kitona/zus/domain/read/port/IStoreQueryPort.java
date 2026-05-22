package org.kitona.zus.domain.read.port;

import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.read.page.CursorPageResult;

import java.util.Optional;

/**
 * Store 读侧查询端口。
 *
 * <p>该接口返回面向展示的 Store 读模型，不承担聚合装载与保存职责。
 */
public interface IStoreQueryPort {

    /**
     * 查询单个 Store 读侧视图。
     *
     * @param storeId 存储空间标识
     * @return 读侧视图，不存在返回 empty
     */
    Optional<StoreView> findViewByStoreId(String storeId);

    /**
     * 游标分页查询 Store 读侧视图。
     *
     * @param pageToken 分页游标
     * @param pageSize  每页大小
     * @return 分页结果
     */
    CursorPageResult<StoreView> findPageViewByCursor(String pageToken, int pageSize);
}
