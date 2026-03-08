package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;

import java.util.List;

/**
 * 授权模型 Mapper 接口
 * 
 * 提供 AuthorizationModel 表的数据访问操作。
 * 授权模型定义了权限检查的规则。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface IAuthorizationModelMapper extends BaseMapper<AuthorizationModelPO> {

    /**
     * 根据存储空间和模型ID查询
     * 
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 授权模型实体
     */
    AuthorizationModelPO selectByModelId(@Param("storeId") String storeId, 
                                         @Param("modelId") String modelId);

    /**
     * 查询存储空间的所有模型
     * 
     * @param storeId 存储空间ID
     * @return 模型列表，按创建时间倒序
     */
    List<AuthorizationModelPO> selectByStoreId(@Param("storeId") String storeId);

    /**
     * 查询存储空间的最新模型
     * 
     * @param storeId 存储空间ID
     * @return 最新的授权模型
     */
    AuthorizationModelPO selectLatestByStoreId(@Param("storeId") String storeId);

    /**
     * 查询存储空间已发布的模型数量
     * 
     * @param storeId 存储空间ID
     * @return 模型数量
     */
    int countPublishedByStoreId(@Param("storeId") String storeId);

    /**
     * 游标分页查询授权模型
     * 
     * @param storeId   存储空间ID
     * @param status    模型状态（可选）: 0-草稿, 1-已发布, 2-已废弃
     * @param pageToken 上一页最后一条的 modelId，首页传 null
     * @param pageSize  每页大小
     * @return 模型列表，按创建时间倒序
     */
    List<AuthorizationModelPO> selectPageByCursor(@Param("storeId") String storeId,
                                                   @Param("status") Integer status,
                                                   @Param("pageToken") String pageToken,
                                                   @Param("pageSize") int pageSize);
}
