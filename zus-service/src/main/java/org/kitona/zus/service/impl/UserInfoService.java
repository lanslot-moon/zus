package org.kitona.zus.service.impl;

import jakarta.annotation.Resource;
import org.kitona.zus.common.utils.OrikaUtils;
import org.kitona.zus.infrastructure.entity.po.UserInfoDo;
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
        UserInfoDo userInfo = userInfoRepository.findByUserId(userId);
        return OrikaUtils.convert(userInfo, UserInfoBO.class);
    }
}
