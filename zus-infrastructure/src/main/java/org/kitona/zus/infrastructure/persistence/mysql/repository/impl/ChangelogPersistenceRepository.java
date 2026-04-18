package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * <p>
 * 简单查询使用 MyBatis Plus，
 * 复杂查询（如 Zookie 范围查询、批量插入、聚合查询等）使用 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class ChangelogPersistenceRepository extends BaseRepository<ChangelogPO>
        implements IChangelogPersistenceRepository {

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 1000;

    @Resource
    private IChangelogMapper changelogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreate(List<ChangelogPO> changelogs) {
        if (changelogs == null || changelogs.isEmpty()) {
            return true;
        }
        return changelogMapper.batchInsert(changelogs) > 0;
    }

    @Override
    public List<ChangelogPO> findByZookieRange(String storeId, Long startZookie,
            Long endZookie, Integer limit) {
        int effectiveLimit = resolveLimit(limit);
        LambdaQueryWrapper<ChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(ChangelogPO::getStoreId, storeId)
                .gt(ChangelogPO::getZookie, startZookie == null ? 0L : startZookie)
                .le(endZookie != null, ChangelogPO::getZookie, endZookie)
                .orderByAsc(ChangelogPO::getZookie);
        return queryPage(wrapper, effectiveLimit);
    }

    @Override
    public List<ChangelogPO> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        int effectiveLimit = resolveLimit(limit);
        LambdaQueryWrapper<ChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(ChangelogPO::getStoreId, storeId)
                .gt(ChangelogPO::getZookie, afterZookie == null ? 0L : afterZookie)
                .orderByAsc(ChangelogPO::getZookie);
        return queryPage(wrapper, effectiveLimit);
    }

    @Override
    public Long getMaxZookie(String storeId) {
        LambdaQueryWrapper<ChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(ChangelogPO::getStoreId, storeId)
                .select(ChangelogPO::getZookie)
                .orderByDesc(ChangelogPO::getZookie);
        List<ChangelogPO> records = queryPage(wrapper, 1);
        if (records.isEmpty() || records.get(0).getZookie() == null) {
            return 0L;
        }
        return records.get(0).getZookie();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupBeforeZookie(String storeId, Long beforeZookie) {
        LambdaQueryWrapper<ChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(ChangelogPO::getStoreId, storeId)
                .lt(ChangelogPO::getZookie, beforeZookie);
        int rows = this.getBaseMapper().delete(wrapper);
        log.info("清理变更日志: storeId={}, beforeZookie={}, count={}", storeId, beforeZookie, rows);
        return rows;
    }

    @Override
    public List<ChangelogPO> findRecentChanges(String storeId, Integer limit) {
        int effectiveLimit = resolveLimit(limit);
        // 先查最大 Zookie
        Long maxZookie = getMaxZookie(storeId);
        if (maxZookie == 0) {
            return Collections.emptyList();
        }
        // 计算起始 Zookie 后走范围查询
        long startZookie = Math.max(0, maxZookie - effectiveLimit);
        return findByZookieRange(storeId, startZookie, maxZookie, effectiveLimit);
    }

    private List<ChangelogPO> queryPage(LambdaQueryWrapper<ChangelogPO> wrapper, int pageSize) {
        Page<ChangelogPO> page = new Page<>(1, pageSize, false);
        return this.page(page, wrapper).getRecords();
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
