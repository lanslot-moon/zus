package org.kitona.zus.infrastructure.cache.provider;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.infrastructure.cache.FgaCacheKey;
import org.kitona.zus.infrastructure.cache.IFgaCacheProvider;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis + Caffeine 多层缓存提供者
 *
 * 实现了 L1 (Caffeine) 和 L2 (Redis) 的两层缓存策略。
 *
 * @author kitona
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "fga.cache.type", havingValue = "redis", matchIfMissing = true)
public class RedisMultiLayerCacheProvider implements IFgaCacheProvider {

    /** TTL 配置 */
    private static final long CHECK_CACHE_TTL_SECONDS = 60;
    private static final long TUPLE_CACHE_TTL_SECONDS = 300;
    private static final long MODEL_CACHE_TTL_SECONDS = 600;

    @Resource(name = "checkResultCache")
    private Cache<String, Boolean> checkResultCache;

    @Resource(name = "modelCache")
    private Cache<String, Object> modelCache;

    @Resource(name = "tupleCache")
    private Cache<String, Object> tupleCache;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean getCheckResult(TupleExistsCacheQuery query) {
        String key = FgaCacheKey.checkResult(query);
        Boolean result = checkResultCache.getIfPresent(key);
        if (result != null) return result;

        Object redisResult = redisTemplate.opsForValue().get(key);
        if (redisResult != null) {
            result = (Boolean) redisResult;
            checkResultCache.put(key, result);
            return result;
        }
        return null;
    }

    @Override
    public void setCheckResult(TupleExistsCacheQuery query, boolean result) {
        String key = FgaCacheKey.checkResult(query);
        checkResultCache.put(key, result);
        redisTemplate.opsForValue().set(key, result, CHECK_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void invalidateCheckCache(String storeId) {
        checkResultCache.invalidateAll();
        String pattern = FgaCacheKey.checkPattern(storeId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public Object getModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        Object model = modelCache.getIfPresent(key);
        if (model != null) return model;

        model = redisTemplate.opsForValue().get(key);
        if (model != null) {
            modelCache.put(key, model);
            return model;
        }
        return null;
    }

    @Override
    public void setModel(String storeId, String modelId, Object model) {
        String key = FgaCacheKey.model(storeId, modelId);
        modelCache.put(key, model);
        redisTemplate.opsForValue().set(key, model, MODEL_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void invalidateModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        String currentKey = FgaCacheKey.currentModel(storeId);
        modelCache.invalidate(key);
        modelCache.invalidate(currentKey);
        redisTemplate.delete(key);
        redisTemplate.delete(currentKey);
    }

    @Override
    public Long incrementZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        return stringRedisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long getCurrentZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    @Override
    public void initZookieIfAbsent(String storeId, long initialValue) {
        String key = FgaCacheKey.zookie(storeId);
        stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(initialValue));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        Object result = tupleCache.getIfPresent(key);
        if (result != null) return (T) result;

        result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            tupleCache.put(key, result);
            return (T) result;
        }
        return null;
    }

    @Override
    public void setTupleQuery(String storeId, String objectType, String objectId, String relation, Object tuples) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);
        tupleCache.put(key, tuples);
        redisTemplate.opsForValue().set(key, tuples, TUPLE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        String pattern = FgaCacheKey.tuplePattern(storeId, objectType, objectId);
        tupleCache.invalidateAll();
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }
}
