package org.kitona.zus.infrastructure.cache;

import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;

/**
 * FGA 缓存 Key 常量和构建工具
 *
 * 统一管理所有缓存 Key 的前缀和构建规则，避免 Key 冲突。
 *
 * Key 命名规范：
 * - 前缀：fga:{业务}:{storeId}:{具体标识}
 * - 使用冒号分隔各层级
 * - 避免使用特殊字符
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public final class FgaCacheKey {

    private FgaCacheKey() {
        // 工具类禁止实例化
    }

    // ==================== 前缀常量 ====================

    /**
     * 根前缀
     */
    public static final String PREFIX = "fga";

    /**
     * Check 结果缓存前缀
     */
    public static final String CHECK_PREFIX = PREFIX + ":check";

    /**
     * 元组缓存前缀
     */
    public static final String TUPLE_PREFIX = PREFIX + ":tuple";

    /**
     * 模型缓存前缀
     */
    public static final String MODEL_PREFIX = PREFIX + ":model";

    /**
     * Zookie 版本前缀
     */
    public static final String ZOOKIE_PREFIX = PREFIX + ":zookie";

    /**
     * 分布式锁前缀
     */
    public static final String LOCK_PREFIX = PREFIX + ":lock";

    // ==================== Key 构建方法 ====================

    /**
     * 构建 Check 结果缓存 Key
     *
     * 格式: fga:check:{storeId}:{hash}
     * 其中 hash 是请求参数的哈希值
     *
     * @param storeId 存储空间ID
     * @param hash    请求参数哈希值
     * @return 缓存 Key
     */
    public static String checkResult(String storeId, String hash) {
        return CHECK_PREFIX + ":" + storeId + ":" + hash;
    }

    /**
     * 构建 Check 结果缓存 Key（使用具体参数）
     *
     * @param storeId         存储空间ID
     * @param objectType      资源类型
     * @param objectId        资源ID
     * @param relation        关系
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系（可为空）
     * @return 缓存 Key
     */
    public static String checkResult(TupleExistsCacheQuery tupleCacheQuery) {
        StringBuilder sb = new StringBuilder(CHECK_PREFIX)
                .append(":").append(tupleCacheQuery.getStoreId())
                .append(":").append(tupleCacheQuery.getObjectType())
                .append(":").append(tupleCacheQuery.getObjectId())
                .append(":").append(tupleCacheQuery.getRelation())
                .append(":").append(tupleCacheQuery.getSubjectType())
                .append(":").append(tupleCacheQuery.getSubjectId());
        if (StringUtils.isNotBlank(tupleCacheQuery.getSubjectRelation())) {
            sb.append(":").append(tupleCacheQuery.getSubjectRelation());
        }
        return sb.toString();
    }

    /**
     * 构建元组查询缓存 Key
     *
     * 格式: fga:tuple:{storeId}:{objectType}:{objectId}:{relation}
     *
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     * @param relation   关系
     * @return 缓存 Key
     */
    public static String tupleQuery(String storeId, String objectType, String objectId, String relation) {
        return TUPLE_PREFIX + ":" + storeId + ":" + objectType + ":" + objectId + ":" + relation;
    }

    /**
     * 构建授权模型缓存 Key
     *
     * 格式: fga:model:{storeId}:{modelId}
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 缓存 Key
     */
    public static String model(String storeId, String modelId) {
        return MODEL_PREFIX + ":" + storeId + ":" + modelId;
    }

    /**
     * 构建当前模型缓存 Key（不带 modelId）
     *
     * 格式: fga:model:{storeId}:current
     *
     * @param storeId 存储空间ID
     * @return 缓存 Key
     */
    public static String currentModel(String storeId) {
        return MODEL_PREFIX + ":" + storeId + ":current";
    }

    /**
     * 构建 Zookie 版本 Key
     *
     * 格式: fga:zookie:{storeId}
     *
     * @param storeId 存储空间ID
     * @return 缓存 Key
     */
    public static String zookie(String storeId) {
        return ZOOKIE_PREFIX + ":" + storeId;
    }

    /**
     * 构建写操作分布式锁 Key
     *
     * 格式: fga:lock:write:{storeId}
     *
     * @param storeId 存储空间ID
     * @return 锁 Key
     */
    public static String writeLock(String storeId) {
        return LOCK_PREFIX + ":write:" + storeId;
    }

    /**
     * 构建 Check 缓存失效模式（用于批量删除）
     *
     * 格式: fga:check:{storeId}:*
     *
     * @param storeId 存储空间ID
     * @return 模式字符串
     */
    public static String checkPattern(String storeId) {
        return CHECK_PREFIX + ":" + storeId + ":*";
    }

    /**
     * 构建元组缓存失效模式
     *
     * 格式: fga:tuple:{storeId}:{objectType}:{objectId}:*
     *
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     * @return 模式字符串
     */
    public static String tuplePattern(String storeId, String objectType, String objectId) {
        return TUPLE_PREFIX + ":" + storeId + ":" + objectType + ":" + objectId + ":*";
    }
}
