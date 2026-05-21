package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ISubjectDefinitionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;

/**
 * 类型定义持久化仓储实现（基础设施层，仅 fga_type_definition 单表）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Repository
public class TypeDefinitionPersistenceRepository extends SoftDeleteRepository<TypeDefinitionPO>
        implements ISubjectDefinitionPersistenceRepository {

    @Override
    public List<TypeDefinitionPO> selectByModelId(String storeId, String modelId) {
        LambdaQueryWrapper<TypeDefinitionPO> wrapper = new LambdaQueryWrapper<TypeDefinitionPO>()
                .eq(StringUtils.isNotBlank(storeId), TypeDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), TypeDefinitionPO::getModelId, modelId)
                .eq(TypeDefinitionPO::getIsDeleted, false);

        List<TypeDefinitionPO> list = this.list(wrapper);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<TypeDefinitionPO> selectByModelIdList(String storeId, Set<String> modelId) {
        if (Objects.isNull(modelId) || modelId.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TypeDefinitionPO> wrapper = new LambdaQueryWrapper<TypeDefinitionPO>()
                .eq(StringUtils.isNotBlank(storeId), TypeDefinitionPO::getStoreId, storeId)
                .in(TypeDefinitionPO::getModelId, modelId)
                .eq(TypeDefinitionPO::getIsDeleted, false)
                .orderByAsc(TypeDefinitionPO::getSortOrder)
                .orderByAsc(TypeDefinitionPO::getId);
        List<TypeDefinitionPO> list = this.list(wrapper);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public Optional<TypeDefinitionPO> selectByType(String storeId, String modelId, String type) {
        LambdaQueryWrapper<TypeDefinitionPO> wrapper = new LambdaQueryWrapper<TypeDefinitionPO>()
                .eq(StringUtils.isNotBlank(storeId), TypeDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), TypeDefinitionPO::getModelId, modelId)
                .eq(StringUtils.isNotBlank(type), TypeDefinitionPO::getSubjectType, type)
                .eq(TypeDefinitionPO::getIsDeleted, false);

        return Optional.ofNullable(this.getOne(wrapper));
    }

    @Override
    public Boolean deleteByModelId(String storeId, String modelId) {
        LambdaUpdateWrapper<TypeDefinitionPO> wrapper = new LambdaUpdateWrapper<TypeDefinitionPO>()
                .set(TypeDefinitionPO::getIsDeleted, true)
                .eq(StringUtils.isNotBlank(storeId), TypeDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), TypeDefinitionPO::getModelId, modelId)
                .eq(TypeDefinitionPO::getIsDeleted, DeletedStatusEnum.NOT_DELETED.getCode());
        return this.update(wrapper);
    }

    @Override
    public Boolean deleteByType(String storeId, String modelId, String type) {
        LambdaUpdateWrapper<TypeDefinitionPO> wrapper = new LambdaUpdateWrapper<TypeDefinitionPO>()
                .set(TypeDefinitionPO::getIsDeleted, true)
                .eq(StringUtils.isNotBlank(storeId), TypeDefinitionPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), TypeDefinitionPO::getModelId, modelId)
                .eq(StringUtils.isNotBlank(type), TypeDefinitionPO::getSubjectType, type)
                .eq(TypeDefinitionPO::getIsDeleted, DeletedStatusEnum.NOT_DELETED.getCode());
        return this.update(wrapper);
    }
}
