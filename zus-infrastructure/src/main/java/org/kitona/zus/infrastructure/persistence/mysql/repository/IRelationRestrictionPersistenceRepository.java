package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;

import java.util.List;
import java.util.Set;

/**
 * 关系类型限制持久化仓储接口（基础设施层，仅 fga_type_restriction 单表）
 *
 * <p>定义基于 PO 的持久化契约，仅供 AuthorizationModelDomainRepositoryAdapter 内部使用。
 * 按照 DDD 规范，外部不应直接访问此接口，所有操作应通过聚合根 Repository 进行。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IRelationRestrictionPersistenceRepository extends IService<TypeRestrictionPO> {

    /**
     * 按关系定义 ID 查询该关系下所有类型限制，未删除，按 id 升序
     *
     * @param relationDefinitionId 关系定义主键 ID（fga_relation_definition.id）
     * @return 关系类型限制列表，无则返回空列表
     */
    List<TypeRestrictionPO> selectByRelationDefinitionId(Long relationDefinitionId);

    /**
     * 根据关系定义ID集合查询关系限制列表
     *
     * @param relationDefinitionIdSet 关系定义ID集合，用于筛选特定关系定义的限制条件
     * @return 返回符合条件的关系限制对象列表(List<TypeRestrictionPO>)
     */
    List<TypeRestrictionPO> selectByRelationDefinitionId(Set<Long> relationDefinitionIdSet);

    /**
     * 按关系定义 ID 逻辑删除该关系下全部类型限制
     *
     * @param relationDefinitionId 关系定义主键 ID
     * @return 是否成功
     */
    boolean deleteByRelationDefinitionId(Long relationDefinitionId);

    /**
     * 按关系定义 ID 列表批量逻辑删除类型限制
     *
     * @param relationDefinitionIds 关系定义主键 ID 列表
     * @return 是否成功
     */
    boolean deleteByRelationDefinitionIds(Set<Long> relationDefinitionIds);
}
