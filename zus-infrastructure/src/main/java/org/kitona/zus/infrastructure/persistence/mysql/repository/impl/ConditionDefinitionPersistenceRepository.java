package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IConditionDefinitionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 条件定义持久化仓储实现。
 */
@Repository
public class ConditionDefinitionPersistenceRepository extends BaseRepository<ConditionDefinitionPO>
        implements IConditionDefinitionPersistenceRepository {

    /**
     * 查询select by model id。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 查询结果
     */
    @Override
    public List<ConditionDefinitionPO> selectByModelId(String storeId, String modelId) {
        LambdaQueryWrapper<ConditionDefinitionPO> wrapper = getLambdaQueryWrapper()
                .eq(ConditionDefinitionPO::getStoreId, storeId)
                .eq(ConditionDefinitionPO::getModelId, modelId)
                .orderByAsc(ConditionDefinitionPO::getId);
        return list(wrapper);
    }

    /**
     * 删除delete by model id。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean deleteByModelId(String storeId, String modelId) {
        LambdaUpdateWrapper<ConditionDefinitionPO> wrapper = new LambdaUpdateWrapper<ConditionDefinitionPO>()
                .set(ConditionDefinitionPO::getIsDeleted, DeletedStatusEnum.DELETED.getCode())
                .eq(ConditionDefinitionPO::getStoreId, storeId)
                .eq(ConditionDefinitionPO::getModelId, modelId)
                .eq(ConditionDefinitionPO::getIsDeleted, DeletedStatusEnum.NOT_DELETED.getCode());
        return update(wrapper);
    }
}
