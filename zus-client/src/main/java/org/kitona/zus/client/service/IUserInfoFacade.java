package org.kitona.zus.client.service;

import org.kitona.zus.client.entity.Result;
import org.kitona.zus.client.entity.dto.UserInfoFacadeDto;

public interface IUserInfoFacade {

    Result<UserInfoFacadeDto> getUserInfo(String userId);
}
