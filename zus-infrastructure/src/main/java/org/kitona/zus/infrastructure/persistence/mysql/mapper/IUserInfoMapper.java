package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;

/**
 * 用户信息 Mapper（MyBatis-Plus）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Mapper
public interface IUserInfoMapper extends BaseMapper<UserInfoPO> {
}
