package org.kitona.zus.domain.read.port;

import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.domain.read.page.CursorPageResult;

import java.util.Optional;

/**
 * 授权模型读侧查询端口。
 *
 * <p>该接口返回面向查询和展示的读模型，不负责装载或保存授权模型聚合根。
 */
public interface IAuthorizationModelQueryPort {

    /**
     * 游标分页查询授权模型读侧视图。
     *
     * @param storeId   存储空间标识
     * @param status    状态过滤，可为 null
     * @param pageToken 分页游标
     * @param pageSize  每页大小
     * @return 分页结果
     */
    CursorPageResult<AuthorizationModelView> findPageViewByCursor(String storeId, Integer status,
                                                                  String pageToken, int pageSize);

    /**
     * 查询存储空间下最新已发布模型的读侧视图。
     *
     * @param storeId 存储空间标识
     * @return 最新已发布模型读侧视图，不存在返回 empty
     */
    Optional<AuthorizationModelView> findLatestPublishedViewByStoreId(String storeId);
}
