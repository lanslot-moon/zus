package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;

import java.util.List;

/**
 * 存储空间 Mapper 接口
 * 
 * 提供 Store 表的数据访问操作，包括基础 CRUD 和自定义查询。
 * 继承 MyBatis-Plus 的 BaseMapper 获得通用方法。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface IStoreMapper extends BaseMapper<StorePO> {

    /**
     * 原子性递增 Zookie 版本号
     * 
     * 使用 UPDATE ... SET 实现原子递增，适用于单机部署。
     * 分布式环境建议使用 Redis INCR 替代。
     * 
     * @param storeId    存储空间ID
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int incrementZookie(@Param("storeId") String storeId, @Param("updateTime") Long updateTime);

    /**
     * 游标分页查询存储空间列表
     *
     * @param pageToken 分页游标（上一页最后一条记录的 storeId），首页传 null
     * @param pageSize  每页大小
     * @return 存储空间列表
     */
    List<StorePO> selectPageByCursor(@Param("pageToken") String pageToken, @Param("pageSize") int pageSize);
}
