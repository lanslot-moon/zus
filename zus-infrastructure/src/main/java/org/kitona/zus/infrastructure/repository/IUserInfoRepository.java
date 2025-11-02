package org.kitona.zus.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.entity.po.UserInfoPo;

public interface IUserInfoRepository extends IService<UserInfoPo> {

    /**
     * 根据用户id查询用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    UserInfoPo findByUserId(String userId);
}
