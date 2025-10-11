package org.kitona.zus.business.service;

import org.kitona.zus.business.entity.dto.UserInfoDTO;

public interface IUserInfoBizService {

    UserInfoDTO getUserInfo(String userId);
}
