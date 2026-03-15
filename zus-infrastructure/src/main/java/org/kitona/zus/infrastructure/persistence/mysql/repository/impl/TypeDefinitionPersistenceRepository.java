package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import org.kitona.zus.infrastructure.persistence.mysql.entity.SubjectDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.ITypeDefinitionMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ISubjectDefinitionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 类型定义持久化仓储实现（基础设施层，仅 fga_type_definition 单表）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Repository
public class TypeDefinitionPersistenceRepository extends BaseRepository<SubjectDefinitionPO>
        implements ISubjectDefinitionPersistenceRepository {

    @Override
    public List<SubjectDefinitionPO> selectByModelId(String storeId, String modelId) {
        LambdaQueryWrapper<SubjectDefinitionPO> wrapper = new LambdaQueryWrapper<SubjectDefinitionPO>()
                .eq(StringUtils.isNotBlank(storeId), SubjectDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), SubjectDefinitionPO::getModelId, modelId)
                .eq(SubjectDefinitionPO::getIsDeleted, false);

        List<SubjectDefinitionPO> list = this.list(wrapper);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<SubjectDefinitionPO> selectByModelIdList(String storeId, Set<String> modelId) {
        return List.of();
    }

    @Override
    public Optional<SubjectDefinitionPO> selectByType(String storeId, String modelId, String type) {
        LambdaQueryWrapper<SubjectDefinitionPO> wrapper = new LambdaQueryWrapper<SubjectDefinitionPO>()
                .eq(StringUtils.isNotBlank(storeId), SubjectDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), SubjectDefinitionPO::getModelId, modelId)
                .eq(StringUtils.isNotBlank(type), SubjectDefinitionPO::getSubjectType, type)
                .eq(SubjectDefinitionPO::getIsDeleted, false);

        return Optional.ofNullable(this.getOne(wrapper));
    }

    @Override
    public Boolean deleteByModelId(String storeId, String modelId) {
        LambdaUpdateWrapper<SubjectDefinitionPO> wrapper = new LambdaUpdateWrapper<SubjectDefinitionPO>()
                .set(SubjectDefinitionPO::getIsDeleted, true)
                .eq(StringUtils.isNotBlank(storeId), SubjectDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), SubjectDefinitionPO::getModelId, modelId);
        return this.update(wrapper);
    }

    @Override
    public Boolean deleteByType(String storeId, String modelId, String type) {
        LambdaUpdateWrapper<SubjectDefinitionPO> wrapper = new LambdaUpdateWrapper<SubjectDefinitionPO>()
                .set(SubjectDefinitionPO::getIsDeleted, true)
                .eq(StringUtils.isNotBlank(storeId), SubjectDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), SubjectDefinitionPO::getModelId, modelId)
                .eq(StringUtils.isNotBlank(type), SubjectDefinitionPO::getSubjectType, type);
        return this.update(wrapper);
    }
}
