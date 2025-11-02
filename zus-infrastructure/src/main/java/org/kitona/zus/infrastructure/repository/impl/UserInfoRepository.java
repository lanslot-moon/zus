package org.kitona.zus.infrastructure.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kitona.zus.infrastructure.entity.po.UserInfoPo;
import org.kitona.zus.infrastructure.mapper.IUserInfoMapper;
import org.kitona.zus.infrastructure.repository.IUserInfoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository extends ServiceImpl<IUserInfoMapper, UserInfoPo> implements IUserInfoRepository {

    @Override
    public UserInfoPo findByUserId(String userId) {
        return getById(userId);
    }
}
