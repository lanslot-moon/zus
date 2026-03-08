package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationRestrictionPO;

/**
 * FGA 关系类型限制 Mapper
 *
 * <p>基础 CRUD 由 MyBatis Plus BaseMapper 提供，
 * Repository 层使用 LambdaQueryWrapper 实现业务查询。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface IRelationRestrictionMapper extends BaseMapper<RelationRestrictionPO> {
}
