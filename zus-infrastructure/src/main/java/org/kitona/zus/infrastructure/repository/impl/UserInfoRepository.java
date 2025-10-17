package org.kitona.zus.infrastructure.repository.impl;

import org.kitona.zus.infrastructure.entity.po.UserInfoDo;
import org.kitona.zus.infrastructure.repository.IUserInfoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository implements IUserInfoRepository {

    @Override
    public UserInfoDo findByUserId(String userId) {
        return null;
    }
}
