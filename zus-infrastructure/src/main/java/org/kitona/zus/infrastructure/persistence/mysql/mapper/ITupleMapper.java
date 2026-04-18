package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;

import java.util.List;

/**
 * 关系元组 Mapper 接口
 * 
 * 提供 Tuple 表的数据访问操作，支持多维度查询。
 * 这是权限检查的核心数据访问接口。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface ITupleMapper extends BaseMapper<TuplePO> {
    int batchInsert(@Param("list") List<TuplePO> list);

}
