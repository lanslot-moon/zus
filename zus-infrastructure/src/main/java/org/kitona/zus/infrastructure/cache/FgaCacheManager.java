package org.kitona.zus.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * FGA 缓存管理器
 *
 * 统一管理 L1（Caffeine 本地缓存）和 L2（Redis 分布式缓存）的读写操作。
 * 采用 Cache-Aside 模式，优先读取 L1，L1 未命中则读取 L2。
 *
 * 缓存层级：应用请求 -> L1 Caffeine -> L2 Redis -> 数据库查询
 *
 * 一致性策略：
 * - 写操作后主动失效相关缓存（L1 和 L2）
 * - L1 使用短 TTL（30-60秒），容忍短暂不一致
 * - L2 使用较长 TTL（1-5分钟），减少数据库压力
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Component
public class FgaCacheManager {

    /**
     * Check 结果缓存 L2 过期时间（秒）
     */
    private static final long CHECK_CACHE_TTL_SECONDS = 60;

    /**
     * 元组缓存 L2 过期时间（秒）
     */
    private static final long TUPLE_CACHE_TTL_SECONDS = 300;

    /**
     * 模型缓存 L2 过期时间（秒）
     */
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

    // ==================== Check 结果缓存操作 ====================

    /**
     * 获取 Check 结果缓存
     *
     * 先查询 L1 本地缓存，未命中则查询 L2 Redis 缓存。
     *
     * @param storeId         存储空间ID
     * @param objectType      资源类型
     * @param objectId        资源ID
     * @param relation        关系
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系
     * @return 缓存的检查结果，未命中返回 null
     */
    public Boolean getCheckResult(TupleExistsCacheQuery tupleCacheQuery) {
        String key = FgaCacheKey.checkResult(tupleCacheQuery);

        // L1: 查询本地缓存
        Boolean result = checkResultCache.getIfPresent(key);
        if (result != null) {
            log.debug("Check 结果命中 L1 缓存: key={}, result={}", key, result);
            return result;
        }

        // L2: 查询 Redis 缓存
        Object redisResult = redisTemplate.opsForValue().get(key);
        if (redisResult != null) {
            result = (Boolean) redisResult;
            // 回填 L1 缓存
            checkResultCache.put(key, result);
            log.debug("Check 结果命中 L2 缓存: key={}, result={}", key, result);
            return result;
        }

        log.debug("Check 结果缓存未命中: key={}", key);
        return Boolean.FALSE;
    }

    /**
     * 设置 Check 结果缓存
     *
     * 同时写入 L1 和 L2 缓存。
     *
     * @param storeId         存储空间ID
     * @param objectType      资源类型
     * @param objectId        资源ID
     * @param relation        关系
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系
     * @param result          检查结果
     */
    public void setCheckResult(TupleExistsCacheQuery tupleCacheQuery, boolean result) {
        String key = FgaCacheKey.checkResult(tupleCacheQuery);

        // 写入 L1 缓存
        checkResultCache.put(key, result);

        // 写入 L2 缓存
        redisTemplate.opsForValue().set(key, result, CHECK_CACHE_TTL_SECONDS, TimeUnit.SECONDS);

        log.debug("Check 结果写入缓存: key={}, result={}", key, result);
    }

    /**
     * 失效指定存储空间的所有 Check 缓存
     *
     * 在 Write 操作后调用，确保缓存一致性。
     *
     * @param storeId 存储空间ID
     */
    public void invalidateCheckCache(String storeId) {
        // 清除 L1 缓存（本地只能清除当前实例）
        checkResultCache.invalidateAll();

        // 清除 L2 缓存（使用 pattern 批量删除）
        String pattern = FgaCacheKey.checkPattern(storeId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除 Check 缓存: storeId={}, count={}", storeId, keys.size());
        }
    }

    // ==================== 模型缓存操作 ====================

    /**
     * 获取授权模型缓存
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 缓存的模型对象，未命中返回 null
     */
    public Object getModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);

        // L1: 查询本地缓存
        Object model = modelCache.getIfPresent(key);
        if (model != null) {
            log.debug("模型命中 L1 缓存: key={}", key);
            return model;
        }

        // L2: 查询 Redis 缓存
        model = redisTemplate.opsForValue().get(key);
        if (model != null) {
            // 回填 L1 缓存
            modelCache.put(key, model);
            log.debug("模型命中 L2 缓存: key={}", key);
            return model;
        }

        return null;
    }

    /**
     * 设置授权模型缓存
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param model   模型对象
     */
    public void setModel(String storeId, String modelId, Object model) {
        String key = FgaCacheKey.model(storeId, modelId);

        // 写入 L1 缓存
        modelCache.put(key, model);

        // 写入 L2 缓存
        redisTemplate.opsForValue().set(key, model, MODEL_CACHE_TTL_SECONDS, TimeUnit.SECONDS);

        log.debug("模型写入缓存: key={}", key);
    }

    /**
     * 失效授权模型缓存
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     */
    public void invalidateModel(String storeId, String modelId) {
        String key = FgaCacheKey.model(storeId, modelId);
        String currentKey = FgaCacheKey.currentModel(storeId);

        modelCache.invalidate(key);
        modelCache.invalidate(currentKey);
        redisTemplate.delete(key);
        redisTemplate.delete(currentKey);

        log.info("失效模型缓存: storeId={}, modelId={}", storeId, modelId);
    }

    // ==================== Zookie 操作 ====================

    /**
     * 原子递增 Zookie 并返回新值
     *
     * 使用 Redis INCR 实现分布式环境下的原子递增。
     *
     * @param storeId 存储空间ID
     * @return 新的 Zookie 值
     */
    public Long incrementZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        Long newZookie = stringRedisTemplate.opsForValue().increment(key);
        log.debug("Zookie 递增: storeId={}, newZookie={}", storeId, newZookie);
        return newZookie;
    }

    /**
     * 获取当前 Zookie 值
     *
     * @param storeId 存储空间ID
     * @return 当前 Zookie 值，不存在返回 0
     */
    public Long getCurrentZookie(String storeId) {
        String key = FgaCacheKey.zookie(storeId);
        String value = stringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    /**
     * 初始化 Zookie（如果不存在）
     *
     * @param storeId      存储空间ID
     * @param initialValue 初始值
     */
    public void initZookieIfAbsent(String storeId, long initialValue) {
        String key = FgaCacheKey.zookie(storeId);
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(initialValue));
        if (Boolean.TRUE.equals(success)) {
            log.info("初始化 Zookie: storeId={}, initialValue={}", storeId, initialValue);
        }
    }

    // ==================== 元组缓存操作 ====================

    /**
     * 获取元组查询缓存
     *
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     * @param relation   关系
     * @return 缓存的元组列表，未命中返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);

        // L1: 查询本地缓存
        Object result = tupleCache.getIfPresent(key);
        if (result != null) {
            return (T) result;
        }

        // L2: 查询 Redis 缓存
        result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            tupleCache.put(key, result);
            return (T) result;
        }

        return null;
    }

    /**
     * 设置元组查询缓存
     *
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     * @param relation   关系
     * @param tuples     元组列表
     */
    public void setTupleQuery(String storeId, String objectType, String objectId,
                              String relation, Object tuples) {
        String key = FgaCacheKey.tupleQuery(storeId, objectType, objectId, relation);

        tupleCache.put(key, tuples);
        redisTemplate.opsForValue().set(key, tuples, TUPLE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 失效元组缓存
     *
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     */
    public void invalidateTupleCache(String storeId, String objectType, String objectId) {
        String pattern = FgaCacheKey.tuplePattern(storeId, objectType, objectId);

        // 清除 L1 缓存
        tupleCache.invalidateAll();

        // 清除 L2 缓存
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }
}
