package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型持久化仓储接口（基础设施层，仅 fga_auth_model 单表）
 *
 * <p>定义基于 PO 的持久化契约，与领域层授权模型聚合仓储职责不同：
 * <ul>
 *   <li>领域层接口：操作聚合根（AuthorizationModelAggregate），定义业务契约</li>
 *   <li>本接口：操作持久化对象（AuthModelPO），定义技术契约</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IAuthorizationModelPersistenceRepository extends IService<AuthModelPO> {

    /**
     * 按存储空间与模型 ID 查询授权模型（仅本表，不含类型定义子表）
     *
     * @param storeId 存储空间 ID
     * @param modelId 模型 ID
     * @return 存在则返回 Optional 包装的 PO，否则 empty
     */
    Optional<AuthModelPO> findByModelIdAndStoreId(String storeId, String modelId);

    /**
     * 查询该存储空间下所有未删除的授权模型，按创建时间倒序
     *
     * @param storeId 存储空间 ID
     * @return 授权模型列表
     */
    List<AuthModelPO> findByStoreId(String storeId);

    /**
     * 查询该存储空间下指定状态的授权模型，按创建时间倒序
     *
     * @param storeId 存储空间 ID
     * @param status  模型状态: 0-草稿, 1-已发布, 2-已废弃
     * @return 授权模型列表
     */
    List<AuthModelPO> findByStoreIdAndStatus(String storeId, Integer status);

    /**
     * 更新授权模型
     *
     * @param model 授权模型持久化对象
     * @return 更新成功返回 true
     */
    boolean updateModel(AuthModelPO model);

    /**
     * 更新授权模型状态
     *
     * @param storeId     存储空间ID
     * @param modelId     模型ID
     * @param status      目标状态
     * @param description 可选，更新描述
     * @return 更新成功返回 true
     */
    boolean updateModelStatus(String storeId, String modelId, Integer status, String description);

    /**
     * 查询该存储空间下最新已发布模型（status=1）
     *
     * @param storeId 存储空间 ID
     * @return 存在则返回 Optional 包装的 PO，否则 empty
     */
    Optional<AuthModelPO> findLatestByStoreId(String storeId);

    /**
     * 创建授权模型记录（草稿状态）
     *
     * @param model 授权模型持久化对象
     */
    void createModel(AuthModelPO model);

    /**
     * 逻辑删除指定草稿模型（is_deleted 置为 1）
     *
     * @param storeId 存储空间 ID
     * @param modelId 模型 ID
     * @return 更新成功返回 true
     */
    boolean deleteDraftModel(String storeId, String modelId);

    /**
     * 游标分页查询授权模型
     *
     * @param storeId   存储空间 ID
     * @param status    模型状态（可选）: 0-草稿, 1-已发布, 2-已废弃
     * @param pageToken 上一页最后一条的 modelId，首页传 null
     * @param pageSize  每页大小
     * @return 模型列表，按创建时间倒序
     */
    List<AuthModelPO> findPageByCursor(String storeId, Integer status, String pageToken, int pageSize);
}
