package org.kitona.zus.facade.service.impl;

import org.kitona.zus.facade.service.IUserInfoAdapterService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoAdapterService implements IUserInfoAdapterService {


    @Override
    public String getUserAddress(String userId) {
        // 进行三方系统的调用
        return "";
    }
}
