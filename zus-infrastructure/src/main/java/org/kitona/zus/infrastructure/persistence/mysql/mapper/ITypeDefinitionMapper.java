package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;

import java.util.List;

/**
 * FGA 类型定义 Mapper
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface ITypeDefinitionMapper extends BaseMapper<TypeDefinitionPO> {

    List<TypeDefinitionPO> selectByModelId(@Param("storeId") String storeId, @Param("modelId") String modelId);

    int deleteByModelId(@Param("storeId") String storeId, @Param("modelId") String modelId, @Param("updateTime") Long updateTime);
}
