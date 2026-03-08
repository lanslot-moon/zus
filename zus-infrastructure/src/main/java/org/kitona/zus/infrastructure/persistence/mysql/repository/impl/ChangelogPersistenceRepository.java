package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IChangelogMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 变更日志持久化仓储实现（基础设施层）
 *
 * <p>简单查询使用 MyBatis Plus，
 * 复杂查询（如 Zookie 范围查询、批量插入、聚合查询等）使用 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class ChangelogPersistenceRepository extends BaseRepository<ChangelogPO> implements IChangelogPersistenceRepository {

    private static final int DEFAULT_LIMIT = 100;

    @Resource
    private IChangelogMapper changelogMapper;

    @Override
    public List<ChangelogPO> findByZookieRange(String storeId, Long startZookie,
                                               Long endZookie, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        // Zookie 范围查询走 XML
        List<ChangelogPO> logs = changelogMapper.selectByZookieRange(storeId, startZookie, endZookie, limit);
        return logs != null ? logs : Collections.emptyList();
    }

    @Override
    public List<ChangelogPO> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        // 增量查询走 XML
        List<ChangelogPO> logs = changelogMapper.selectAfterZookie(storeId, afterZookie, limit);
        return logs != null ? logs : Collections.emptyList();
    }

    @Override
    public Long getMaxZookie(String storeId) {
        Long maxZookie = changelogMapper.selectMaxZookie(storeId);
        return maxZookie != null ? maxZookie : 0L;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupBeforeZookie(String storeId, Long beforeZookie) {
        // 批量删除走 XML
        int rows = changelogMapper.cleanupBeforeZookie(storeId, beforeZookie);
        log.info("清理变更日志: storeId={}, beforeZookie={}, count={}", storeId, beforeZookie, rows);
        return rows;
    }

    @Override
    public List<ChangelogPO> findRecentChanges(String storeId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        // 先查最大 Zookie
        Long maxZookie = getMaxZookie(storeId);
        if (maxZookie == 0) {
            return Collections.emptyList();
        }
        // 计算起始 Zookie 后走范围查询
        long startZookie = Math.max(0, maxZookie - limit);
        return findByZookieRange(storeId, startZookie, maxZookie, limit);
    }
}
