package org.kitona.zus.service.impl;

import jakarta.annotation.Resource;
import org.kitona.zus.common.utils.OrikaUtil;
import org.kitona.zus.infrastructure.entity.po.UserInfoPo;
import org.kitona.zus.infrastructure.repository.IUserInfoRepository;
import org.kitona.zus.service.IUserInfoService;
import org.kitona.zus.service.entity.bo.UserInfoBO;
import org.springframework.stereotype.Service;


@Service
public class UserInfoService implements IUserInfoService {

    @Resource
    private IUserInfoRepository userInfoRepository;

    @Override
    public UserInfoBO getUserInfo(String userId) {
        UserInfoPo userInfo = userInfoRepository.findByUserId(userId);
        return OrikaUtil.convert(userInfo, UserInfoBO.class);
    }
}
