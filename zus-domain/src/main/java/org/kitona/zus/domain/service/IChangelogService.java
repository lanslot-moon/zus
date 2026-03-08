package org.kitona.zus.domain.service;

import org.kitona.zus.domain.entity.ChangelogEntity;

import java.util.List;

/**
 * 变更日志领域服务接口
 *
 * <p>Changelog 不是聚合根，而是事件日志记录，因此定义为领域服务而非 Repository。
 *
 * <p>提供变更日志的记录和查询能力，用于：
 * <ul>
 *   <li>Zookie 一致性读取</li>
 *   <li>Watch API 实时监听</li>
 *   <li>审计追踪</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IChangelogService {

    /**
     * 批量记录变更日志
     *
     * @param changelogs 变更日志实体列表
     */
    void recordChanges(List<ChangelogEntity> changelogs);

    /**
     * 记录单条变更日志
     *
     * @param changelog 变更日志实体
     */
    void recordChange(ChangelogEntity changelog);

    /**
     * 按 Zookie 范围查询变更日志，用于 Watch 增量拉取
     *
     * @param storeId     存储空间ID
     * @param startZookie 起始 Zookie（不包含）
     * @param endZookie   结束 Zookie（包含），传 null 表示到最新
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogEntity> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的变更，用于 Watch 轮询
     *
     * @param storeId     存储空间ID
     * @param afterZookie 起始 Zookie（不包含）
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogEntity> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 获取该存储空间当前最大 Zookie
     *
     * @param storeId 存储空间ID
     * @return 最大 Zookie，无记录返回 0
     */
    Long getMaxZookie(String storeId);

    /**
     * 查询最近若干条变更
     *
     * @param storeId 存储空间ID
     * @param limit   最大返回条数
     * @return 变更日志列表
     */
    List<ChangelogEntity> findRecentChanges(String storeId, Integer limit);

    /**
     * 清理指定 Zookie 之前的变更日志
     *
     * @param storeId      存储空间ID
     * @param beforeZookie 清理该 Zookie 之前的记录
     * @return 删除行数
     */
    int cleanupBeforeZookie(String storeId, Long beforeZookie);
}
