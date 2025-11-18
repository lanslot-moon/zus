package org.kitona.zus.service.domain.user;

import org.kitona.zus.service.domain.user.entity.bo.UserInfoBO;

public interface IUserInfoService {

    UserInfoBO getUserInfo(String userId);
}
