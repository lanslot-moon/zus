package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.BaseSoftDeletePO;

/**
 * 带逻辑删除过滤条件的基础仓储。
 */
public class SoftDeleteRepository<T extends BaseSoftDeletePO> extends BaseRepository<T> {

    @Override
    protected LambdaQueryWrapper<T> getLambdaQueryWrapper() {
        return super.getLambdaQueryWrapper()
                .eq(BaseSoftDeletePO::getIsDeleted, DeletedStatusEnum.NOT_DELETED.getCode());
    }
}
