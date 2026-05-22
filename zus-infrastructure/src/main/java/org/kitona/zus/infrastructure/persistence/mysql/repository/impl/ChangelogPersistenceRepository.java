package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;
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
public class ChangelogPersistenceRepository extends BaseRepository<TupleChangelogPO>
        implements IChangelogPersistenceRepository {

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 1000;

    @Resource
    private IChangelogMapper changelogMapper;

    /**
     * 批量创建持久化记录。
     *
     * @param changelogs 变更日志列表
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreate(List<TupleChangelogPO> changelogs) {
        if (changelogs == null || changelogs.isEmpty()) {
            return true;
        }
        return changelogMapper.batchInsert(changelogs) > 0;
    }

    /**
     * 按 zookie 闭区间查询变更日志。
     *
     * @param storeId     Store 标识
     * @param startZookie 起始 zookie
     * @param endZookie   结束 zookie
     * @param limit       查询条数上限
     * @return 变更日志持久化记录列表
     */
    @Override
    public List<TupleChangelogPO> findByZookieRange(String storeId, Long startZookie,
            Long endZookie, Integer limit) {
        int effectiveLimit = resolveLimit(limit);
        LambdaQueryWrapper<TupleChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(TupleChangelogPO::getStoreId, storeId)
                .gt(TupleChangelogPO::getZookie, startZookie == null ? 0L : startZookie)
                .le(endZookie != null, TupleChangelogPO::getZookie, endZookie)
                .orderByAsc(TupleChangelogPO::getZookie);
        return queryPage(wrapper, effectiveLimit);
    }

    /**
     * 查询指定 zookie 之后的变更日志。
     *
     * @param storeId Store 标识
     * @param afterZookie afterZookie 参数
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<TupleChangelogPO> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        int effectiveLimit = resolveLimit(limit);
        LambdaQueryWrapper<TupleChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(TupleChangelogPO::getStoreId, storeId)
                .gt(TupleChangelogPO::getZookie, afterZookie == null ? 0L : afterZookie)
                .orderByAsc(TupleChangelogPO::getZookie);
        return queryPage(wrapper, effectiveLimit);
    }

    /**
     * 查询当前最大 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Long getMaxZookie(String storeId) {
        LambdaQueryWrapper<TupleChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(TupleChangelogPO::getStoreId, storeId)
                .select(TupleChangelogPO::getZookie)
                .orderByDesc(TupleChangelogPO::getZookie);
        List<TupleChangelogPO> records = queryPage(wrapper, 1);
        if (records.isEmpty() || records.get(0).getZookie() == null) {
            return 0L;
        }
        return records.get(0).getZookie();
    }

    /**
     * 清理指定 zookie 之前的变更日志。
     *
     * @param storeId Store 标识
     * @param beforeZookie beforeZookie 参数
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupBeforeZookie(String storeId, Long beforeZookie) {
        LambdaQueryWrapper<TupleChangelogPO> wrapper = getLambdaQueryWrapper()
                .eq(TupleChangelogPO::getStoreId, storeId)
                .lt(TupleChangelogPO::getZookie, beforeZookie);
        int rows = this.getBaseMapper().delete(wrapper);
        log.info("清理变更日志: storeId={}, beforeZookie={}, count={}", storeId, beforeZookie, rows);
        return rows;
    }

    /**
     * 查询最近的变更日志。
     *
     * @param storeId Store 标识
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<TupleChangelogPO> findRecentChanges(String storeId, Integer limit) {
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

    /**
     * 查询query page。
     *
     * @param wrapper wrapper 参数
     * @param pageSize 分页大小
     * @return 查询结果
     */
    private List<TupleChangelogPO> queryPage(LambdaQueryWrapper<TupleChangelogPO> wrapper, int pageSize) {
        Page<TupleChangelogPO> page = new Page<>(1, pageSize, false);
        return this.page(page, wrapper).getRecords();
    }

    /**
     * 解析查询条数上限。
     *
     * @param limit limit 参数
     * @return 构建结果
     */
    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
