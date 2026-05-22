package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.enums.StoreStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IStoreMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IStorePersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 存储空间持久化仓储实现（基础设施层）
 *
 * <p>
 * 简单查询使用 MyBatis Plus LambdaQueryWrapper，
 * 复杂查询（如原子递增、特定状态过滤等）使用 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class StorePersistenceRepository extends BaseRepository<StorePO> implements IStorePersistenceRepository {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;

    @Resource
    private IStoreMapper storeMapper;

    @Resource
    private FgaCacheManager cacheManager;

    /**
     * 按 Store 标识查询记录。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Optional<StorePO> findByStoreId(String storeId) {
        LambdaQueryWrapper<StorePO> wrapper = getLambdaQueryWrapper()
                .eq(StorePO::getStoreId, storeId);

        StorePO store = super.getOne(wrapper);
        return Optional.ofNullable(store);
    }

    /**
     * 按状态查询 Store 记录。
     *
     * @param status 状态
     * @return 查询结果
     */
    @Override
    public List<StorePO> findByStatus(Integer status) {
        LambdaQueryWrapper<StorePO> wrapper = getLambdaQueryWrapper()
                .eq(StorePO::getStatus, status)
                .orderByDesc(StorePO::getCreateTime);

        return this.list(wrapper);
    }

    /**
     * 创建create store。
     *
     * @param store Store 聚合
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createStore(StorePO store) {
        boolean success = this.save(store);
        if (!success) {
            return false;
        }
        cacheManager.initZookieIfAbsent(store.getStoreId(), 0L);
        log.info("创建存储空间: storeId={}, name={}", store.getStoreId(), store.getName());
        return true;
    }

    /**
     * 生成next zookie。
     *
     * @param storeId Store 标识
     * @return 返回结果
     */
    @Override
    public Long nextZookie(String storeId) {
        // 先从缓存递增
        Long newZookie = cacheManager.incrementZookie(storeId);
        // 原子递增走 XML
        storeMapper.incrementZookie(storeId, System.currentTimeMillis());
        return newZookie;
    }

    /**
     * 读取 Store 当前 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Long getCurrentZookie(String storeId) {
        // 先查缓存
        Long zookie = cacheManager.getCurrentZookie(storeId);
        if (zookie > 0) {
            return zookie;
        }

        LambdaQueryWrapper<StorePO> wrapper = getLambdaQueryWrapper()
                .eq(StorePO::getStoreId, storeId)
                .eq(StorePO::getStatus, StoreStatusEnum.NORMAL.getCode());

        StorePO result = super.getOne(wrapper);
        if (result == null || result.getCurrentZookie() == null) {
            return 0L;
        }

        cacheManager.initZookieIfAbsent(storeId, result.getCurrentZookie());
        return result.getCurrentZookie();
    }

    /**
     * 删除delete by store id。
     *
     * @param storeId Store 标识
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByStoreId(String storeId) {
        LambdaQueryWrapper<StorePO> wrapper = getLambdaQueryWrapper().eq(StorePO::getStoreId, storeId);

        StorePO storePO = new StorePO();
        storePO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());

        boolean success = this.update(storePO, wrapper);
        if (!success) {
            return false;
        }
        cacheManager.invalidateCheckCache(storeId);
        return true;
    }

    /**
     * 更新存储空间
     *
     * @param store 存储空间 PO
     * @return 更新成功返回 true
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStore(StorePO store) {
        LambdaUpdateWrapper<StorePO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(StorePO::getStoreId, store.getStoreId());
        boolean updated = this.update(store, wrapper);
        if (!updated) {
            return false;
        }
        if (StringUtils.isNotBlank(store.getCurrentModelId())) {
            cacheManager.invalidateModel(store.getStoreId(), store.getCurrentModelId());
        }
        return true;
    }

    /**
     * 按游标分页查询记录。
     *
     * @param pageToken 分页游标
     * @param pageSize 分页大小
     * @return 查询结果
     */
    @Override
    public List<StorePO> findPageByCursor(String pageToken, int pageSize) {
        LambdaQueryWrapper<StorePO> wrapper = getLambdaQueryWrapper()
                .lt(StringUtils.isNotBlank(pageToken), StorePO::getStoreId, pageToken)
                .orderByDesc(StorePO::getStoreId);
        Page<StorePO> page = new Page<>(1, resolvePageSize(pageSize), false);
        return this.page(page, wrapper).getRecords();
    }

    /**
     * 解析安全分页大小。
     *
     * @param pageSize 分页大小
     * @return 构建结果
     */
    private int resolvePageSize(int pageSize) {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
