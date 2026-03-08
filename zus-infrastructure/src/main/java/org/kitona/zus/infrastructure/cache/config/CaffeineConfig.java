package org.kitona.zus.infrastructure.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置类
 * 
 * 提供高性能的本地缓存，作为 L1 缓存层。
 * 相比 Redis（L2），本地缓存具有更低的访问延迟。
 * 
 * 缓存策略：
 * - Check 结果缓存：TTL 30秒，容量 10000
 * - 模型缓存：TTL 5分钟，模型变更时主动失效
 * - 元组缓存：TTL 60秒，容量 50000
 * 
 * 注意事项：
 * - 本地缓存不具备分布式一致性，适合短 TTL 场景
 * - Write 操作后需要主动失效相关缓存
 * - 多实例部署时，各实例缓存独立
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Configuration
public class CaffeineConfig {

    /**
     * Check 结果缓存 Bean 名称
     */
    public static final String CHECK_RESULT_CACHE = "checkResultCache";

    /**
     * 授权模型缓存 Bean 名称
     */
    public static final String MODEL_CACHE = "modelCache";

    /**
     * 元组缓存 Bean 名称
     */
    public static final String TUPLE_CACHE = "tupleCache";

    /**
     * Check 结果本地缓存
     * 
     * 缓存权限检查结果，Key 为 "storeId:objectType:objectId:relation:subjectType:subjectId:subjectRelation"。
     * 
     * 配置说明：
     * - expireAfterWrite: 写入后 30 秒过期
     * - maximumSize: 最大缓存 10000 条记录
     * - recordStats: 开启统计，便于监控缓存命中率
     * 
     * @return Check 结果缓存实例
     */
    @Bean(CHECK_RESULT_CACHE)
    public Cache<String, Boolean> checkResultCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(10000)
                .recordStats()
                .build();
    }

    /**
     * 授权模型本地缓存
     * 
     * 缓存编译后的授权模型图，Key 为 "storeId:modelId"。
     * 模型变更时需要主动调用 invalidate 方法失效缓存。
     * 
     * 配置说明：
     * - expireAfterWrite: 写入后 5 分钟过期
     * - maximumSize: 最大缓存 100 个模型
     * 
     * @return 授权模型缓存实例
     */
    @Bean(MODEL_CACHE)
    public Cache<String, Object> modelCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats()
                .build();
    }

    /**
     * 元组查询结果本地缓存
     * 
     * 缓存元组查询结果，用于加速 TTU 等高频查询。
     * Key 为 "storeId:objectType:objectId:relation"。
     * 
     * 配置说明：
     * - expireAfterWrite: 写入后 60 秒过期
     * - maximumSize: 最大缓存 50000 条查询结果
     * 
     * @return 元组缓存实例
     */
    @Bean(TUPLE_CACHE)
    public Cache<String, Object> tupleCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(50000)
                .recordStats()
                .build();
    }
}
