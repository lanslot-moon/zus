package org.kitona.zus.infrastructure.repository;

import org.kitona.zus.infrastructure.entity.po.UserInfoDo;

public interface IUserInfoRepository {
    UserInfoDo findByUserId(String userId);
}
