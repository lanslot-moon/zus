package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.entity.ChangelogEntity;

import java.util.List;

/**
 * 变更日志查询仓储接口（读侧）
 *
 * <p>承接 Watch、增量同步和一致性 token 等查询能力。
 */
public interface IChangelogQueryRepository {

    /**
     * 按 Zookie 范围查询变更日志
     */
    List<ChangelogEntity> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的增量变更
     */
    List<ChangelogEntity> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 获取存储空间当前最大的 Zookie 版本号
     */
    Long getMaxZookie(String storeId);

    /**
     * 查询最近生成的变更日志
     */
    List<ChangelogEntity> findRecentChanges(String storeId, Integer limit);
}
