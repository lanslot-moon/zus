package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;

/**
 * 条件定义 Mapper —— {@code fga_condition_definition}
 *
 * <p>作为 {@link org.kitona.zus.infrastructure.persistence.mysql.repository.impl.ConditionDefinitionPersistenceRepository}
 * 所依赖的 {@code BaseMapper<ConditionDefinitionPO>} 实现，由 {@code @MapperScan} 自动注册。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Mapper
public interface IConditionDefinitionMapper extends BaseMapper<ConditionDefinitionPO> {
}
