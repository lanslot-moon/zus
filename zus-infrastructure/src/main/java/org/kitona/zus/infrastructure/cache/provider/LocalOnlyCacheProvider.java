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

    @Override
    public Boolean getCheckResult(TupleExistsCacheQuery query) {
        String key = FgaCacheKey.checkResult(query);
        return checkResultCache.getIfPresent(key);
    }

    @Override
    public void setCheckResult(TupleExistsCacheQuery query, boolean result) {
        String key = FgaCacheKey.checkResult(query);
        checkResultCache.put(key, result);
    }

    @Override
    public void invalidateCheckCache(String storeId) {
        checkResultCache.invalidateAll();
    }

    @Override
    public Object getModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        return modelCache.getIfPresent(key);
    }

    @Override
    public void setModel(String storeId, String modelId, Object model) {
        String key = FgaCacheKey.model(storeId, modelId);
        modelCache.put(key, model);
    }

    @Override
    public void invalidateModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        String currentKey = FgaCacheKey.currentModel(storeId);
        modelCache.invalidate(key);
        modelCache.invalidate(currentKey);
    }

    @Override
    public Long incrementZookie(String storeId) {
        return zookieMap.computeIfAbsent(storeId, k -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Long getCurrentZookie(String storeId) {
        AtomicLong counter = zookieMap.get(storeId);
        return counter != null ? counter.get() : 0L;
    }

    @Override
    public void initZookieIfAbsent(String storeId, long initialValue) {
        zookieMap.putIfAbsent(storeId, new AtomicLong(initialValue));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        return (T) tupleCache.getIfPresent(key);
    }

    @Override
    public void setTupleQuery(String storeId, String objectType, String objectId, String relation, Object tuples) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        tupleCache.put(key, tuples);
    }

    @Override
    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        tupleCache.invalidateAll();
    }
}
