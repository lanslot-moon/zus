package org.kitona.zus.infrastructure.cache;

import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;

/**
 * FGA 缓存提供者接口
 *
 * 定义缓存层级的核心操作逻辑，支持不同存储介质（如 Redis, Caffeine）的实现。
 *
 * @author kitona
 */
public interface IFgaCacheProvider {

    // ==================== Check 结果缓存 ====================

    /**
     * 获取 Check 结果缓存
     */
    Boolean getCheckResult(TupleExistsCacheQuery query);

    /**
     * 设置 Check 结果缓存
     */
    void setCheckResult(TupleExistsCacheQuery query, boolean result);

    /**
     * 失效指定存储空间的 Check 缓存
     */
    void invalidateCheckCache(String storeId);

    // ==================== 授权模型缓存 ====================

    /**
     * 获取模型缓存
     */
    Object getModel(String storeId, String modelId);

    /**
     * 设置模型缓存
     */
    void setModel(String storeId, String modelId, Object model);

    /**
     * 失效模型缓存
     */
    void invalidateModel(String storeId, String modelId);

    // ==================== Zookie/版本 缓存 ====================

    /**
     * 原子递增 Zookie
     */
    Long incrementZookie(String storeId);

    /**
     * 获取当前 Zookie
     */
    Long getCurrentZookie(String storeId);

    /**
     * 初始化 Zookie
     */
    void initZookieIfAbsent(String storeId, long initialValue);

    // ==================== 元组列表缓存 ====================

    /**
     * 获取元组查询缓存
     */
    <T> T getTupleQuery(String storeId, String objectType, String objectId, String relation);

    /**
     * 设置元组查询缓存
     */
    void setTupleQuery(String storeId, String objectType, String objectId, String relation, Object tuples);

    /**
     * 失效元组缓存
     */
    void invalidateTupleCache(String storeId, String objectType, String objectId);
}
