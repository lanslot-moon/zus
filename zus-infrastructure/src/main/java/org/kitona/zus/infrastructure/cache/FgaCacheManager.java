package org.kitona.zus.infrastructure.cache;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.infrastructure.cache.provider.LocalOnlyCacheProvider;
import org.kitona.zus.infrastructure.cache.provider.RedisMultiLayerCacheProvider;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;
import org.springframework.stereotype.Component;

/**
 * FGA 缓存管理器 (Entry Point)
 *
 * 统一管理缓存操作的对外入口。采用策略模式，将具体的读写逻辑委托给 {@link IFgaCacheProvider}。
 * 这种设计允许我们在不同环境或配置下，动态切换底层缓存存储（如从 Redis 降级为本地 Caffeine）。
 *
 * @author kitona
 * @see IFgaCacheProvider
 * @see RedisMultiLayerCacheProvider
 * @see LocalOnlyCacheProvider
 */
@Slf4j
@Component
public class FgaCacheManager {

    @Resource
    private IFgaCacheProvider cacheProvider;


    public Boolean getCheckResult(TupleExistsCacheQuery tupleCacheQuery) {
        return cacheProvider.getCheckResult(tupleCacheQuery);
    }

    public void setCheckResult(TupleExistsCacheQuery tupleCacheQuery, boolean result) {
        cacheProvider.setCheckResult(tupleCacheQuery, result);
    }

    public void invalidateCheckCache(String storeId) {
        cacheProvider.invalidateCheckCache(storeId);
    }

    // ==================== 模型缓存操作 ====================

    public Object getModel(String storeId, String modelId) {
        return cacheProvider.getModel(storeId, modelId);
    }

    public void setModel(String storeId, String modelId, Object model) {
        cacheProvider.setModel(storeId, modelId, model);
    }

    public void invalidateModel(String storeId, String modelId) {
        cacheProvider.invalidateModel(storeId, modelId);
    }

    // ==================== Zookie 操作 ====================

    /**
     * 原子递增 Zookie 并返回新值
     */
    public Long incrementZookie(String storeId) {
        return cacheProvider.incrementZookie(storeId);
    }

    public Long getCurrentZookie(String storeId) {
        return cacheProvider.getCurrentZookie(storeId);
    }

    public void initZookieIfAbsent(String storeId, long initialValue) {
        cacheProvider.initZookieIfAbsent(storeId, initialValue);
    }

    // ==================== 元组缓存操作 ====================

    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        return cacheProvider.getTupleQuery(storeId, objectType, objectId, relation);
    }

    public void setTupleQuery(String storeId, String objectType, String objectId,
                               String relation, Object tuples) {
        cacheProvider.setTupleQuery(storeId, objectType, objectId, relation, tuples);
    }

    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        cacheProvider.invalidateTupleCache(storeId, objectType, objectId);
    }
}
