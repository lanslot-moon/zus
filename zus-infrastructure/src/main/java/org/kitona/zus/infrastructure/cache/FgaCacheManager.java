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


    /**
     * 读取权限检查缓存结果。
     *
     * @param tupleCacheQuery tupleCacheQuery 参数
     * @return 查询结果
     */
    public Boolean getCheckResult(TupleExistsCacheQuery tupleCacheQuery) {
        return cacheProvider.getCheckResult(tupleCacheQuery);
    }

    /**
     * 设置set check result。
     *
     * @param tupleCacheQuery tupleCacheQuery 参数
     * @param result 结果对象
     */
    public void setCheckResult(TupleExistsCacheQuery tupleCacheQuery, boolean result) {
        cacheProvider.setCheckResult(tupleCacheQuery, result);
    }

    /**
     * 失效指定 Store 的权限检查缓存。
     *
     * @param storeId Store 标识
     */
    public void invalidateCheckCache(String storeId) {
        cacheProvider.invalidateCheckCache(storeId);
    }

    // ==================== 模型缓存操作 ====================

    /**
     * 查询授权模型详情。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 查询结果
     */
    public Object getModel(String storeId, String modelId) {
        return cacheProvider.getModel(storeId, modelId);
    }

    /**
     * 设置set model。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @param model 授权模型聚合
     */
    public void setModel(String storeId, String modelId, Object model) {
        cacheProvider.setModel(storeId, modelId, model);
    }

    /**
     * 失效模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     */
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

    /**
     * 读取 Store 当前 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    public Long getCurrentZookie(String storeId) {
        return cacheProvider.getCurrentZookie(storeId);
    }

    /**
     * 初始化init zookie if absent。
     *
     * @param storeId Store 标识
     * @param initialValue initialValue 参数
     */
    public void initZookieIfAbsent(String storeId, long initialValue) {
        cacheProvider.initZookieIfAbsent(storeId, initialValue);
    }

    // ==================== 元组缓存操作 ====================

    /**
     * 读取 tuple 查询缓存。
     *
     * @param storeId Store 标识
     * @param objectType 对象类型
     * @param objectId 对象标识
     * @param relation 关系名
     * @return 查询结果
     */
    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        return cacheProvider.getTupleQuery(storeId, objectType, objectId, relation);
    }

    /**
     * 写入 tuple 查询缓存。
     *
     * @param storeId    Store 标识
     * @param objectType 对象类型
     * @param objectId   对象标识
     * @param relation   关系名
     * @param tuples     tuple 查询结果
     */
    public void setTupleQuery(String storeId, String objectType, String objectId,
                               String relation, Object tuples) {
        cacheProvider.setTupleQuery(storeId, objectType, objectId, relation, tuples);
    }

    /**
     * 失效 tuple 查询缓存。
     *
     * @param storeId Store 标识
     * @param objectType 对象类型
     * @param objectId 对象标识
     */
    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        cacheProvider.invalidateTupleCache(storeId, objectType, objectId);
    }
}
