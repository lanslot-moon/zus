package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IRelationRestrictionPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 关系类型限制持久化仓储实现（基础设施层，仅 fga_type_restriction 单表）
 *
 * <p>
 * 简单查询使用 MyBatis Plus LambdaQueryWrapper，
 * 无需 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Repository
public class RelationRestrictionPersistenceRepository extends BaseRepository<TypeRestrictionPO>
        implements IRelationRestrictionPersistenceRepository {

    @Override
    public List<TypeRestrictionPO> selectByRelationDefinitionId(Long relationDefinitionId) {
        LambdaQueryWrapper<TypeRestrictionPO> wrapper = getLambdaQueryWrapper()
                .eq(TypeRestrictionPO::getRelationDefinitionId, relationDefinitionId)
                .orderByAsc(TypeRestrictionPO::getId);

        return this.list(wrapper);
    }

    @Override
    public List<TypeRestrictionPO> selectByRelationDefinitionId(Set<Long> relationDefinitionIdSet) {
        if (Objects.isNull(relationDefinitionIdSet) || relationDefinitionIdSet.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TypeRestrictionPO> wrapper = getLambdaQueryWrapper()
                .in(TypeRestrictionPO::getRelationDefinitionId, relationDefinitionIdSet)
                .eq(TypeRestrictionPO::getIsDeleted, false)
                .orderByAsc(TypeRestrictionPO::getId);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRelationDefinitionId(Long relationDefinitionId) {
        LambdaQueryWrapper<TypeRestrictionPO> wrapper = getLambdaQueryWrapper()
                .eq(TypeRestrictionPO::getRelationDefinitionId, relationDefinitionId);

        TypeRestrictionPO updatePO = new TypeRestrictionPO();
        updatePO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());

        return this.update(updatePO, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRelationDefinitionIds(Set<Long> relationDefinitionIds) {
        if (CollectionUtils.isEmpty(relationDefinitionIds)) {
            return true;
        }
        LambdaQueryWrapper<TypeRestrictionPO> wrapper = getLambdaQueryWrapper()
                .in(TypeRestrictionPO::getRelationDefinitionId, relationDefinitionIds);

        TypeRestrictionPO updatePO = new TypeRestrictionPO();
        updatePO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());

        return this.update(updatePO, wrapper);
    }
}
