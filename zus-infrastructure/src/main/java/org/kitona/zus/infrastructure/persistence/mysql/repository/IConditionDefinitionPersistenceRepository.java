package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;

import java.util.List;

/**
 * 条件定义持久化仓储。
 */
public interface IConditionDefinitionPersistenceRepository extends IService<ConditionDefinitionPO> {

    List<ConditionDefinitionPO> selectByModelId(String storeId, String modelId);

    boolean deleteByModelId(String storeId, String modelId);
}
