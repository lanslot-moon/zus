package org.kitona.zus.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.entity.po.UserInfoDo;

public interface IUserInfoRepository extends IService<UserInfoDo> {

    /**
     * 根据用户id查询用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    UserInfoDo findByUserId(String userId);

//    UserInfoDo findByNameAndAge(String name, Integer age);
}
