package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.entity.ChangelogEntity;

import java.util.List;

/**
 * 变更日志领域仓储接口
 *
 * <p>
 * Changelog 记录了元组（Tuple）的所有变更历史，是实现 Zookie 一致性读取和 Watch API 的核心。
 * 按照 DDD 规范，Repository 负责领域实体 {@link ChangelogEntity} 的持久化与查询。
 *
 * @author kitona
 * @version 1.1.0
 * @since 2025-03-15
 */
public interface IChangelogDomainRepository {

    /**
     * 批量持久化变更日志
     *
     * @param changelogs 变更日志实体列表
     */
    void saveBatch(List<ChangelogEntity> changelogs);

    /**
     * 持久化单条变更日志
     *
     * @param changelog 变更日志实体
     */
    void save(ChangelogEntity changelog);

    /**
     * 按 Zookie 范围查询变更日志（用于从特定点开始同步数据）
     *
     * @param storeId     存储空间ID
     * @param startZookie 起始 Zookie（不包含）
     * @param endZookie   结束 Zookie（包含），传 null 表示到最新
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogEntity> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的增量变更（用于 Watch API 实时监听）
     *
     * @param storeId     存储空间ID
     * @param afterZookie 起始 Zookie（不包含）
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogEntity> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 获取存储空间当前最大的 Zookie 版本号
     *
     * @param storeId 存储空间ID
     * @return 最大 Zookie，无记录返回 0
     */
    Long getMaxZookie(String storeId);

    /**
     * 查询最近生成的变更日志
     *
     * @param storeId 存储空间ID
     * @param limit   最大返回条数
     * @return 变更日志列表
     */
    List<ChangelogEntity> findRecentChanges(String storeId, Integer limit);

    /**
     * 清理历史变更日志
     *
     * @param storeId      存储空间ID
     * @param beforeZookie 清理该 Zookie 之前的记录（不包含）
     * @return 删除的记录行数
     */
    int deleteBeforeZookie(String storeId, Long beforeZookie);
}
