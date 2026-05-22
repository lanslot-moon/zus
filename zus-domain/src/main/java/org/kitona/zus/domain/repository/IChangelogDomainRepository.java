package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.audit.Changelog;

import java.util.List;

/**
 * 变更日志领域仓储接口
 *
 * <p>
 * Changelog 记录了元组（Tuple）的所有变更历史，是实现 Zookie 一致性读取和 Watch API 的核心。
 * 按照 DDD 规范，Repository 负责领域实体 {@link Changelog} 的写侧持久化；
 * Watch、增量同步与 zookie 查询能力交由独立查询仓储处理。
 *
 * @author kitona
 * @version 1.1.0
 * @since 2025-03-15
 */
public interface IChangelogDomainRepository {

    /**
     * 持久化单条变更日志
     *
     * @param changelog 变更日志实体
     */
    void save(Changelog changelog);

    /**
     * 批量持久化变更日志。
     *
     * @param changelogs 变更日志实体列表
     */
    void saveAll(List<Changelog> changelogs);
}
