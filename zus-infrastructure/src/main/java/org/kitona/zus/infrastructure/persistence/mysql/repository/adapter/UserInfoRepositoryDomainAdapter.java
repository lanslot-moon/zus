package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.entity.UserInfoEntity;
import org.kitona.zus.domain.repository.IUserInfoDomainRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/3/15 17:44
 * Version: V1.0
 * Description: Xxxx
 */
@Service
public class UserInfoRepositoryDomainAdapter implements IUserInfoDomainRepository {
    @Override
    public Optional<UserInfoEntity> findByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public boolean save(UserInfoEntity userInfo) {
        return false;
    }

    @Override
    public boolean existsByUserId(String userId) {
        return false;
    }
}
