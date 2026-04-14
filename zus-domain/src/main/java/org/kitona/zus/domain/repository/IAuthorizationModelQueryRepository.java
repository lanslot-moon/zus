package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.domain.valueobject.CursorPageResult;

import java.util.Optional;

/**
 * 授权模型查询仓储接口（读侧）
 */
public interface IAuthorizationModelQueryRepository {

    /**
     * 游标分页查询授权模型读侧视图
     *
     * @param storeId   存储空间ID
     * @param status    状态过滤，可为 null
     * @param pageToken 分页游标
     * @param pageSize  每页大小
     * @return 分页结果
     */
    CursorPageResult<AuthorizationModelView> findPageViewByCursor(String storeId, Integer status, String pageToken, int pageSize);

    /**
     * 查询存储空间下最新已发布模型的读侧视图
     */
    Optional<AuthorizationModelView> findLatestPublishedViewByStoreId(String storeId);
}
