package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 类型定义持久化仓储接口（基础设施层，仅 fga_type_definition 单表）
 *
 * <p>定义基于 PO 的持久化契约，仅供 AuthorizationModelDomainRepositoryAdapter 内部使用。
 * 按照 DDD 规范，外部不应直接访问此接口，所有操作应通过聚合根 Repository 进行。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface ISubjectDefinitionPersistenceRepository extends IService<TypeDefinitionPO> {

    /**
     * 按存储空间与模型 ID 查询该模型下所有类型定义，未删除，按 sort_order 与 id 升序
     *
     * @param storeId 存储空间 ID
     * @param modelId 授权模型 ID
     * @return 类型定义列表，无则返回空列表
     */
    List<TypeDefinitionPO> selectByModelId(String storeId, String modelId);


    /**
     * 按存储空间与模型 ID 查询该模型下所有类型定义，未删除，按 sort_order 与 id 升序
     *
     * @param storeId 存储空间 ID
     * @param modelId 授权模型 ID
     * @return 类型定义列表，无则返回空列表
     */
    List<TypeDefinitionPO> selectByModelIdList(String storeId, Set<String> modelId);

    /**
     * 按存储空间、模型 ID 和类型名查询类型定义
     *
     * @param storeId 存储空间 ID
     * @param modelId 授权模型 ID
     * @param type    类型名
     * @return 类型定义
     */
    Optional<TypeDefinitionPO> selectByType(String storeId, String modelId, String type);

    /**
     * 按存储空间与模型 ID 逻辑删除该模型下全部类型定义
     *
     * @param storeId    存储空间 ID
     * @param modelId    授权模型 ID
     * @return 是否成功
     */
    Boolean deleteByModelId(String storeId, String modelId);

    /**
     * 按存储空间、模型 ID 和类型名逻辑删除类型定义
     *
     * @param storeId 存储空间 ID
     * @param modelId 授权模型 ID
     * @param type    类型名
     * @return 是否成功
     */
    Boolean deleteByType(String storeId, String modelId, String type);
}
