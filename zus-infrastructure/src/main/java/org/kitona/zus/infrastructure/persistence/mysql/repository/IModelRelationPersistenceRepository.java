package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ModelRelationPO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 关系定义持久化仓储接口（基础设施层，仅 fga_model_relation 单表）
 *
 * <p>定义基于 PO 的持久化契约，仅供 AuthorizationModelRepositoryDomainAdapter 内部使用。
 * 按照 DDD 规范，外部不应直接访问此接口，所有操作应通过聚合根 Repository 进行。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IModelRelationPersistenceRepository extends IService<ModelRelationPO> {

    /**
     * 按类型定义 ID 查询该类型下所有关系定义，未删除，按 id 升序
     *
     * @param typeDefinitionId 类型定义主键 ID
     * @return 关系定义列表，无则返回空列表
     */
    List<ModelRelationPO> selectByTypeDefinitionId(Long typeDefinitionId);

    /**
     * 按类型定义 ID 查询该类型下所有关系定义，未删除，按 id 升序
     *
     * @param typeDefinitionId 类型定义主键 ID
     * @return 关系定义列表，无则返回空列表
     */
    List<ModelRelationPO> selectByTypeDefinitionId(Set<Long> typeDefinitionId);

    /**
     * 按类型定义 ID 和关系名查询关系定义
     *
     * @param typeDefinitionId 类型定义主键 ID
     * @param relationName     关系名
     * @return 关系定义
     */
    Optional<ModelRelationPO> selectByRelationName(Long typeDefinitionId, String relationName);

    /**
     * 按类型定义 ID 逻辑删除该类型下全部关系定义
     *
     * @param typeDefinitionId 类型定义主键 ID
     * @return 是否成功
     */
    boolean deleteByTypeDefinitionId(Long typeDefinitionId);

    /**
     * 按类型定义 ID 逻辑删除该类型下全部关系定义
     *
     * @param typeDefinitionIds 类型定义主键 ID
     * @return 是否成功
     */
    boolean deleteByTypeDefinitionIds(Set<Long> typeDefinitionIds);
}
