package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.domain.enums.StoreStatus;

import java.util.List;
import java.util.Optional;

/**
 * 存储空间仓储接口（领域层）
 * 
 * <p>Store 是 FGA 系统的顶层聚合根，代表一个权限数据的逻辑隔离单元。
 * 每个 Store 包含独立的授权模型、关系元组和变更日志。
 * 
 * <p>按照 DDD 严格规范，Repository 操作聚合根 {@link StoreAggregate}。
 * 只定义不实现，实现在基础设施层。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IStoreDomainRepository {

    /**
     * 根据存储空间ID查询聚合根
     *
     * @param storeId 存储空间唯一标识
     * @return 存储空间聚合根，不存在返回 empty
     */
    Optional<StoreAggregate> findByStoreId(String storeId);

    /**
     * 根据状态查询存储空间列表
     *
     * @param status 存储空间状态
     * @return 存储空间聚合根列表
     */
    List<StoreAggregate> findByStatus(StoreStatus status);

    /**
     * 保存存储空间聚合根（新增或更新）
     *
     * @param store 存储空间聚合根
     * @return 保存成功返回 true
     */
    boolean saveOrUpdateStore(StoreAggregate store);

    /**
     * 更新存储空间当前使用的授权模型
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型ID
     * @return 更新成功返回 true
     */
    boolean updateCurrentModelId(String storeId, String modelId);

    /**
     * 获取下一个 Zookie 版本号（原子递增）
     * 
     * <p>Zookie 是一致性令牌，用于实现快照读取。
     * 每次写入操作后递增，确保读取操作能看到指定版本之前的所有变更。
     *
     * @param storeId 存储空间ID
     * @return 递增后的 Zookie 版本号
     */
    Long nextZookie(String storeId);

    /**
     * 获取当前 Zookie 版本号
     *
     * @param storeId 存储空间ID
     * @return 当前 Zookie 版本号，不存在返回 0
     */
    Long getCurrentZookie(String storeId);

    /**
     * 删除存储空间（逻辑删除，标记为删除中状态）
     *
     * @param storeId 存储空间ID
     * @return 删除成功返回 true
     */
    boolean deleteByStoreId(String storeId);

    /**
     * 检查存储空间是否存在
     *
     * @param storeId 存储空间ID
     * @return 存在返回 true
     */
    boolean existsByStoreId(String storeId);
}
