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

    /**
     * 根据资源和关系查询元组列表
     * 
     * 用于 TTU（Tuple To Userset）查询，查找资源的所有关联对象。
     * 例如：查找 document:1 的所有 parent 关系。
     * 
     * @param storeId    存储空间ID
     * @param objectType 资源类型
     * @param objectId   资源ID
     * @param relation   关系名称
     * @param maxZookie  最大 Zookie 版本
     * @return 元组列表
     */
    List<TuplePO> selectByObjectAndRelation(@Param("storeId") String storeId,
                                            @Param("objectType") String objectType,
                                            @Param("objectId") String objectId,
                                            @Param("relation") String relation,
                                            @Param("maxZookie") Long maxZookie);
}
