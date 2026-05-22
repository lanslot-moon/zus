package org.kitona.zus.domain.read.port;

import org.kitona.zus.domain.read.view.ChangelogView;

import java.util.List;

/**
 * 变更日志读侧查询端口。
 *
 * <p>该接口承接 Watch 和增量同步所需的只读查询，不负责变更日志写入。
 */
public interface IChangelogQueryPort {

    /**
     * 按 Zookie 范围查询变更日志。
     *
     * @param storeId     存储空间标识
     * @param startZookie 起始 Zookie
     * @param endZookie   结束 Zookie
     * @param limit       查询条数上限
     * @return 变更日志列表
     */
    List<ChangelogView> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的增量变更。
     *
     * @param storeId     存储空间标识
     * @param afterZookie 起始 Zookie，不包含
     * @param limit       查询条数上限
     * @return 变更日志列表
     */
    List<ChangelogView> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 查询最近生成的变更日志。
     *
     * @param storeId 存储空间标识
     * @param limit   查询条数上限
     * @return 变更日志列表
     */
    List<ChangelogView> findRecentChanges(String storeId, Integer limit);
}
