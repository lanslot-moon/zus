package org.kitona.zus.infrastructure.cache.provider;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.infrastructure.cache.FgaCacheKey;
import org.kitona.zus.infrastructure.cache.IFgaCacheProvider;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 仅本地缓存提供者
 *
 * 适用于单机环境或不需要分布式缓存的场景。核心逻辑复用 Caffeine 缓存。
 * Zookie 递增逻辑使用内部并发 Map 模拟。
 *
 * @author kitona
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "fga.cache.type", havingValue = "local")
public class LocalOnlyCacheProvider implements IFgaCacheProvider {

    @Resource(name = "checkResultCache")
    private Cache<String, Boolean> checkResultCache;

    @Resource(name = "modelCache")
    private Cache<String, Object> modelCache;

    @Resource(name = "tupleCache")
    private Cache<String, Object> tupleCache;

    /**
     * Zookie 存储（本地模拟）
     */
    private final ConcurrentHashMap<String, AtomicLong> zookieMap = new ConcurrentHashMap<>();

    /**
     * 读取权限检查缓存结果。
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public Boolean getCheckResult(TupleExistsCacheQuery query) {
        String key = FgaCacheKey.checkResult(query);
        return checkResultCache.getIfPresent(key);
    }

    /**
     * 设置set check result。
     *
     * @param query 查询条件
     * @param result 结果对象
     */
    @Override
    public void setCheckResult(TupleExistsCacheQuery query, boolean result) {
        String key = FgaCacheKey.checkResult(query);
        checkResultCache.put(key, result);
    }

    /**
     * 失效指定 Store 的权限检查缓存。
     *
     * @param storeId Store 标识
     */
    @Override
    public void invalidateCheckCache(String storeId) {
        checkResultCache.invalidateAll();
    }

    /**
     * 查询授权模型详情。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 查询结果
     */
    @Override
    public Object getModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        return modelCache.getIfPresent(key);
    }

    /**
     * 设置set model。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @param model 授权模型聚合
     */
    @Override
    public void setModel(String storeId, String modelId, Object model) {
        String key = FgaCacheKey.model(storeId, modelId);
        modelCache.put(key, model);
    }

    /**
     * 失效模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     */
    @Override
    public void invalidateModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        String currentKey = FgaCacheKey.currentModel(storeId);
        modelCache.invalidate(key);
        modelCache.invalidate(currentKey);
    }

    /**
     * 递增increment zookie。
     *
     * @param storeId Store 标识
     * @return 返回结果
     */
    @Override
    public Long incrementZookie(String storeId) {
        return zookieMap.computeIfAbsent(storeId, k -> new AtomicLong(0)).incrementAndGet();
    }

    /**
     * 读取 Store 当前 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Long getCurrentZookie(String storeId) {
        AtomicLong counter = zookieMap.get(storeId);
        return counter != null ? counter.get() : 0L;
    }

    /**
     * 初始化init zookie if absent。
     *
     * @param storeId Store 标识
     * @param initialValue initialValue 参数
     */
    @Override
    public void initZookieIfAbsent(String storeId, long initialValue) {
        zookieMap.putIfAbsent(storeId, new AtomicLong(initialValue));
    }

    /**
     * 读取 tuple 查询缓存。
     *
     * @param storeId Store 标识
     * @param objectType 对象类型
     * @param objectId 对象标识
     * @param relation 关系名
     * @return 查询结果
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        return (T) tupleCache.getIfPresent(key);
    }

    /**
     * 设置set tuple query。
     *
     * @param storeId Store 标识
     * @param objectType 对象类型
     * @param objectId 对象标识
     * @param relation 关系名
     * @param tuples 关系元组列表
     */
    @Override
    public void setTupleQuery(String storeId, String objectType, String objectId, String relation, Object tuples) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        tupleCache.put(key, tuples);
    }

    /**
     * 失效 tuple 查询缓存。
     *
     * @param storeId Store 标识
     * @param objectType 对象类型
     * @param objectId 对象标识
     */
    @Override
    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        tupleCache.invalidateAll();
    }
}
