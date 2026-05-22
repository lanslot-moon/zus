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

    /**
     * 读取权限检查缓存结果。
     *
     * @param query 查询条件
     * @return 查询结果
     */
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
        redisTemplate.opsForValue().set(key, result, CHECK_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 失效指定 Store 的权限检查缓存。
     *
     * @param storeId Store 标识
     */
    @Override
    public void invalidateCheckCache(String storeId) {
        checkResultCache.invalidateAll();
        String pattern = FgaCacheKey.checkPattern(storeId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
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
        Object model = modelCache.getIfPresent(key);
        if (model != null) return model;

        model = redisTemplate.opsForValue().get(key);
        if (model != null) {
            modelCache.put(key, model);
            return model;
        }
        return null;
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
        redisTemplate.opsForValue().set(key, model, MODEL_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
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
        redisTemplate.delete(key);
        redisTemplate.delete(currentKey);
    }

    /**
     * 递增increment zookie。
     *
     * @param storeId Store 标识
     * @return 返回结果
     */
    @Override
    public Long incrementZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 读取 Store 当前 zookie。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Long getCurrentZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    /**
     * 初始化init zookie if absent。
     *
     * @param storeId Store 标识
     * @param initialValue initialValue 参数
     */
    @Override
    public void initZookieIfAbsent(String storeId, long initialValue) {
        String key = FgaCacheKey.zookie(storeId);
        stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(initialValue));
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
        Object result = tupleCache.getIfPresent(key);
        if (result != null) return (T) result;

        result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            tupleCache.put(key, result);
            return (T) result;
        }
        return null;
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
        redisTemplate.opsForValue().set(key, tuples, TUPLE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
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
        String pattern = FgaCacheKey.tuplePattern(storeId, objectType, objectId);
        tupleCache.invalidateAll();
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }
}
