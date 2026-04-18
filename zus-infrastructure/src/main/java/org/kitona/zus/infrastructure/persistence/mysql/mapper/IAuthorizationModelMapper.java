package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;

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
}
