package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.micrometer.common.util.StringUtils;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IModelRelationPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 关系定义持久化仓储实现（基础设施层，仅 fga_relation_definition 单表）
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
public class ModelRelationPersistenceRepository extends BaseRepository<RelationDefinitionPO>
        implements IModelRelationPersistenceRepository {

    /**
     * 查询select by type definition id。
     *
     * @param typeDefinitionId typeDefinitionId 参数
     * @return 查询结果
     */
    @Override
    public List<RelationDefinitionPO> selectByTypeDefinitionId(Long typeDefinitionId) {
        LambdaQueryWrapper<RelationDefinitionPO> wrapper = getLambdaQueryWrapper()
                .eq(RelationDefinitionPO::getTypeDefinitionId, typeDefinitionId)
                .orderByAsc(RelationDefinitionPO::getId);

        return this.list(wrapper);
    }

    /**
     * 查询select by type definition id。
     *
     * @param typeDefinitionId typeDefinitionId 参数
     * @return 查询结果
     */
    @Override
    public List<RelationDefinitionPO> selectByTypeDefinitionId(Set<Long> typeDefinitionId) {
        if (Objects.isNull(typeDefinitionId) || typeDefinitionId.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<RelationDefinitionPO> wrapper = getLambdaQueryWrapper()
                .in(RelationDefinitionPO::getTypeDefinitionId, typeDefinitionId)
                .eq(RelationDefinitionPO::getIsDeleted, false)
                .orderByAsc(RelationDefinitionPO::getId);
        return this.list(wrapper);
    }

    /**
     * 查询select by relation name。
     *
     * @param typeDefinitionId typeDefinitionId 参数
     * @param relationName relationName 参数
     * @return 查询结果
     */
    @Override
    public Optional<RelationDefinitionPO> selectByRelationName(Long typeDefinitionId, String relationName) {
        LambdaQueryWrapper<RelationDefinitionPO> wrapper = getLambdaQueryWrapper()
                .eq(RelationDefinitionPO::getTypeDefinitionId, typeDefinitionId)
                .eq(StringUtils.isNotBlank(relationName), RelationDefinitionPO::getRelationName, relationName);

        return Optional.ofNullable(this.getOne(wrapper));
    }

    /**
     * 删除delete by type definition id。
     *
     * @param typeDefinitionId typeDefinitionId 参数
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByTypeDefinitionId(Long typeDefinitionId) {
        LambdaQueryWrapper<RelationDefinitionPO> wrapper = getLambdaQueryWrapper()
                .eq(RelationDefinitionPO::getTypeDefinitionId, typeDefinitionId);

        RelationDefinitionPO modelRelationPO = new RelationDefinitionPO();
        modelRelationPO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());
        return this.update(modelRelationPO, wrapper);
    }

    /**
     * 删除delete by type definition ids。
     *
     * @param typeDefinitionIds typeDefinitionIds 参数
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean deleteByTypeDefinitionIds(Set<Long> typeDefinitionIds) {
        if (Objects.isNull(typeDefinitionIds) || typeDefinitionIds.isEmpty()) {
            return true;
        }
        LambdaQueryWrapper<RelationDefinitionPO> wrapper = getLambdaQueryWrapper()
                .in(RelationDefinitionPO::getTypeDefinitionId, typeDefinitionIds);

        RelationDefinitionPO modelRelationPO = new RelationDefinitionPO();
        modelRelationPO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());
        return this.update(modelRelationPO, wrapper);
    }
}
