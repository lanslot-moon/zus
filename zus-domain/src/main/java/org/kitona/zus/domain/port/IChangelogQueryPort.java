package org.kitona.zus.domain.port;

import org.kitona.zus.domain.entity.ChangelogEntity;

import java.util.List;

/**
 * 变更日志查询端口
 *
 * <p>Changelog 不是聚合根，而是记录关系元组变更的事件日志。
 *
 * <p>变更日志支持以下业务场景：
 * <ul>
 *   <li>Watch 功能：客户端可以监听指定 Store 的元组变更</li>
 *   <li>增量同步：基于 Zookie 实现增量拉取</li>
 *   <li>审计追踪：追溯权限变更历史</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IChangelogQueryPort {

    /**
     * 查询指定 Zookie 范围内的变更日志
     *
     * @param storeId     存储空间ID
     * @param startZookie 起始 Zookie（包含）
     * @param endZookie   结束 Zookie（包含）
     * @param limit       最大返回数量
     * @return 变更日志列表，按 zookie 升序排列
     */
    List<ChangelogEntity> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的变更日志
     *
     * @param storeId     存储空间ID
     * @param afterZookie 起始 Zookie（不包含）
     * @param limit       最大返回数量
     * @return 变更日志列表，按 zookie 升序排列
     */
    List<ChangelogEntity> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 获取存储空间当前最大的 Zookie
     *
     * @param storeId 存储空间ID
     * @return 最大 Zookie，无变更时返回 0
     */
    Long getMaxZookie(String storeId);

    /**
     * 清理指定 Zookie 之前的历史变更日志
     *
     * @param storeId      存储空间ID
     * @param beforeZookie 截止 Zookie（不包含）
     * @return 删除的记录数
     */
    int cleanupBeforeZookie(String storeId, Long beforeZookie);

    /**
     * 查询最近的变更日志
     *
     * @param storeId 存储空间ID
     * @param limit   最大返回数量
     * @return 变更日志列表，按 zookie 降序排列
     */
    List<ChangelogEntity> findRecentChanges(String storeId, Integer limit);

    /**
     * 批量记录变更日志
     *
     * @param changelogs 变更日志列表
     * @return 记录成功返回 true
     */
    boolean recordChanges(List<ChangelogEntity> changelogs);
}
