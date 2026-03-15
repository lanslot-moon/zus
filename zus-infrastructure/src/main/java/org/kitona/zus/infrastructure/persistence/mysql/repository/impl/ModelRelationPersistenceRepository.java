package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.micrometer.common.util.StringUtils;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ModelRelationPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IModelRelationMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IModelRelationPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 关系定义持久化仓储实现（基础设施层，仅 fga_model_relation 单表）
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
public class ModelRelationPersistenceRepository extends BaseRepository<ModelRelationPO>
        implements IModelRelationPersistenceRepository {

    @Override
    public List<ModelRelationPO> selectByTypeDefinitionId(Long typeDefinitionId) {
        LambdaQueryWrapper<ModelRelationPO> wrapper = getLambdaQueryWrapper()
                .eq(ModelRelationPO::getTypeDefinitionId, typeDefinitionId)
                .orderByAsc(ModelRelationPO::getId);

        return this.list(wrapper);
    }

    @Override
    public List<ModelRelationPO> selectByTypeDefinitionId(Set<Long> typeDefinitionId) {
        return List.of();
    }

    @Override
    public Optional<ModelRelationPO> selectByRelationName(Long typeDefinitionId, String relationName) {
        LambdaQueryWrapper<ModelRelationPO> wrapper = getLambdaQueryWrapper()
                .eq(ModelRelationPO::getTypeDefinitionId, typeDefinitionId)
                .eq(StringUtils.isNotBlank(relationName), ModelRelationPO::getRelationName, relationName);

        return Optional.ofNullable(this.getOne(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByTypeDefinitionId(Long typeDefinitionId) {
        LambdaQueryWrapper<ModelRelationPO> wrapper = getLambdaQueryWrapper()
                .eq(ModelRelationPO::getTypeDefinitionId, typeDefinitionId);

        ModelRelationPO modelRelationPO = new ModelRelationPO();
        modelRelationPO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());
        return this.update(modelRelationPO, wrapper);
    }

    @Override
    public boolean deleteByTypeDefinitionIds(Set<Long> typeDefinitionIds) {
        return false;
    }
}
