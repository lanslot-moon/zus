package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;

import java.util.List;
import java.util.Optional;

/**
 * 存储空间持久化仓储接口（基础设施层）
 *
 * <p>
 * 定义基于 PO 的持久化契约，与领域层 {@link IStoreDomainRepository} 职责不同：
 * <ul>
 * <li>领域层接口：操作聚合根（StoreAggregate），定义业务契约</li>
 * <li>本接口：操作持久化对象（StorePO），定义技术契约</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IStorePersistenceRepository extends IService<StorePO> {

    /**
     * 根据存储空间唯一标识查询
     *
     * @param storeId 存储空间唯一标识
     * @return 存在则返回 Optional 包装的 StorePO，否则 empty
     */
    Optional<StorePO> findByStoreId(String storeId);

    /**
     * 根据状态查询存储空间列表，仅返回未删除且状态匹配的记录，按创建时间倒序
     *
     * @param status 状态值，0 正常 1 禁用 2 删除中
     * @return 存储空间列表
     */
    List<StorePO> findByStatus(Integer status);

    /**
     * 创建存储空间，并初始化 Zookie 版本号
     *
     * @param store 存储空间持久化对象
     * @return 创建成功返回 true
     */
    boolean createStore(StorePO store);

    /**
     * 更新当前使用的授权模型 ID
     *
     * @param storeId 存储空间 ID
     * @param modelId 新的授权模型 ID
     * @return 更新成功返回 true
     */
    boolean updateCurrentModelId(String storeId, String modelId);

    /**
     * 获取下一个 Zookie 版本号并递增（用于写元组时打版本）
     *
     * @param storeId 存储空间 ID
     * @return 新的 Zookie 版本号
     */
    Long nextZookie(String storeId);

    /**
     * 获取当前 Zookie 版本号（用于一致性读取）
     *
     * @param storeId 存储空间 ID
     * @return 当前 Zookie 值，无则返回 0
     */
    Long getCurrentZookie(String storeId);

    /**
     * 按存储空间 ID 逻辑删除（置 is_deleted=1、status=2）
     *
     * @param storeId 存储空间 ID
     * @return 删除成功返回 true
     */
    boolean deleteByStoreId(String storeId);

    /**
     * 检查存储空间 ID 是否存在且未删除
     *
     * @param storeId 存储空间 ID
     * @return 存在且未删除返回 true
     */
    boolean existsByStoreId(String storeId);

    /**
     * 游标分页查询存储空间列表（仅 NORMAL 状态）
     *
     * @param pageToken 分页游标（上一页最后一条记录的 storeId），首页传 null
     * @param pageSize  每页大小
     * @return 存储空间列表
     */
    List<StorePO> findPageByCursor(String pageToken, int pageSize);
}
