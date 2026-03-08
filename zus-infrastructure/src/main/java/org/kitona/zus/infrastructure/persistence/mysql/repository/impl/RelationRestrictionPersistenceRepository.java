package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IRelationRestrictionPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 关系类型限制持久化仓储实现（基础设施层，仅 fga_relation_restriction 单表）
 *
 * <p>简单查询使用 MyBatis Plus LambdaQueryWrapper，
 * 无需 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class RelationRestrictionPersistenceRepository extends BaseRepository<RelationRestrictionPO> implements IRelationRestrictionPersistenceRepository {

    @Override
    public List<RelationRestrictionPO> selectByRelationDefinitionId(Long relationDefinitionId) {
        LambdaQueryWrapper<RelationRestrictionPO> wrapper = getLambdaQueryWrapper()
                .eq(RelationRestrictionPO::getRelationDefinitionId, relationDefinitionId)
                .orderByAsc(RelationRestrictionPO::getId);

        return this.list(wrapper);
    }

    @Override
    public List<RelationRestrictionPO> selectByRelationDefinitionId(Set<Long> relationDefinitionIdSet) {
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRelationDefinitionId(Long relationDefinitionId) {
        LambdaQueryWrapper<RelationRestrictionPO> wrapper = getLambdaQueryWrapper()
                .eq(RelationRestrictionPO::getRelationDefinitionId, relationDefinitionId);

        RelationRestrictionPO updatePO = new RelationRestrictionPO();
        updatePO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());

        return this.update(updatePO, wrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRelationDefinitionIds(Set<Long> relationDefinitionIds) {
        if (CollectionUtils.isEmpty(relationDefinitionIds)) {
            return true;
        }
        LambdaQueryWrapper<RelationRestrictionPO> wrapper = getLambdaQueryWrapper()
                .in(RelationRestrictionPO::getRelationDefinitionId, relationDefinitionIds);

        RelationRestrictionPO updatePO = new RelationRestrictionPO();
        updatePO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());

        return this.update(updatePO, wrapper);
    }
}
