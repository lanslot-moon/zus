package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.query.ListStoresQuery;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.StoreResultDTO;

/**
 * Store 应用服务接口
 *
 * 提供存储空间的创建、查询、删除、列表用例，委托领域仓储实现真实落库。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IStoreApplicationService {

    /**
     * 创建存储空间
     *
     * @param name        名称
     * @param description 描述，可为空
     * @return 创建结果，包含系统生成的 storeId
     */
    StoreResultDTO createStore(String name, String description);

    /**
     * 获取存储空间
     *
     * @param storeId 存储空间ID
     * @return 存在则返回结果，否则 empty
     */
    StoreResultDTO getStore(String storeId);

    /**
     * 禁用存储空间
     *
     * <p>
     * 状态流转：NORMAL → DISABLE
     * <p>
     * 禁用后 Store 不可用于权限检查，但数据保留，可随时恢复。
     *
     * @param storeId 存储空间ID
     * @return 禁用成功返回 true
     */
    boolean disableStore(String storeId);

    /**
     * 启用存储空间
     *
     * <p>
     * 状态流转：DISABLE → NORMAL
     * <p>
     * 启用后 Store 恢复正常使用。
     *
     * @param storeId 存储空间ID
     * @return 启用成功返回 true
     */
    boolean enableStore(String storeId);

    /**
     * 删除存储空间（逻辑删除）
     *
     * <p>
     * 前置条件：Store 必须是 DISABLE 状态
     * <p>
     * 必须先禁用再删除，删除后不可恢复。
     *
     * @param storeId 存储空间ID
     * @return 删除成功返回 true
     */
    boolean deleteStore(String storeId);

    /**
     * 游标分页列出存储空间
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResultDTO<StoreResultDTO> listStores(ListStoresQuery query);
}
