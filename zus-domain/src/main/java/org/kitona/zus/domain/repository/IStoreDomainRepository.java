package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.domain.enums.StoreStatus;
import java.util.List;
import java.util.Optional;

/**
 * 存储空间仓储接口（领域层）
 * 
 * <p>
 * Store 是 FGA 系统中的 store 级聚合根，代表一个权限数据的逻辑隔离单元。
 * 它负责管理 store 自身元数据、状态以及当前生效模型指针。
 * 
 * <p>
 * 按照 DDD 规范，Repository 负责聚合根 {@link StoreAggregate} 的装载与保存。
 * 跨聚合协调行为（如 zookie 序列生成）以及列表/展示型查询
 * 由独立的查询仓储处理，不在该接口中定义。
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
     * 删除存储空间（逻辑删除，标记为删除中状态）
     *
     * @param storeId 存储空间ID
     * @return 删除成功返回 true
     */
    boolean deleteByStoreId(String storeId);

}
