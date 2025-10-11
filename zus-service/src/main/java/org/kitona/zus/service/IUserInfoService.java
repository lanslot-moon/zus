package org.kitona.zus.service;

import org.kitona.zus.service.entity.bo.UserInfoBO;

public interface IUserInfoService {

    UserInfoBO getUserInfo(String userId);
}
