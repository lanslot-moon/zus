package org.kitona.zus.infrastructure.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kitona.zus.infrastructure.entity.po.UserInfoDo;
import org.kitona.zus.infrastructure.mapper.IUserInfoMapper;
import org.kitona.zus.infrastructure.repository.IUserInfoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserInfoRepository extends ServiceImpl<IUserInfoMapper, UserInfoDo> implements IUserInfoRepository {

    @Override
    public UserInfoDo findByUserId(String userId) {
        return getById(userId);
    }

//    @Override
//    public UserInfoDo findByNameAndAge(String name, Integer age) {
//        return this.baseMapper.findByNameAndAge(name, age);
//    }
}
