/**
 * 基础设施层缓存能力
 *
 * <p>提供 Zookie 版本号、Tuple 存在性等缓存，降低数据库压力并保证一致性。
 * 包含 Caffeine 本地缓存与 Redis 分布式缓存配置及查询封装。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure.cache;
