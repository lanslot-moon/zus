package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.entity.UserInfoEntity;
import org.kitona.zus.domain.repository.IUserInfoDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IUserInfoPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * 用户信息仓储领域接口适配器。
 */
@Repository
public class UserInfoDomainRepositoryAdapter implements IUserInfoDomainRepository {

    @Resource
    private IUserInfoPersistenceRepository userInfoPersistenceRepository;

    @Override
    public Optional<UserInfoEntity> findByUserId(String userId) {
        return userInfoPersistenceRepository.findByUserId(userId)
                .map(this::toEntity);
    }

    @Override
    public boolean save(UserInfoEntity userInfo) {
        if (userInfo == null || StringUtils.isBlank(userInfo.getUserId())) {
            return false;
        }
        return userInfoPersistenceRepository.saveOrUpdate(toPO(userInfo));
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userInfoPersistenceRepository.existsByUserId(userId);
    }

    private UserInfoEntity toEntity(UserInfoPO po) {
        if (po == null) {
            return null;
        }
        return UserInfoEntity.builder()
                .userId(po.getUserId())
                .userName(po.getUserName())
                .userRole(po.getUserRole())
                .userStatus(po.getUserStatus())
                .userPhone(po.getUserPhone())
                .userEmail(po.getUserEmail())
                .userAddress(po.getUserAddress())
                .userAvatar(po.getUserAvatar())
                .createTime(toLocalDateTime(po.getCreateTime()))
                .updateTime(toLocalDateTime(po.getUpdateTime()))
                .build();
    }

    private UserInfoPO toPO(UserInfoEntity entity) {
        UserInfoPO po = new UserInfoPO();
        po.setUserId(entity.getUserId());
        po.setUserName(entity.getUserName());
        po.setUserRole(entity.getUserRole());
        po.setUserStatus(entity.getUserStatus());
        po.setUserPhone(entity.getUserPhone());
        po.setUserEmail(entity.getUserEmail());
        po.setUserAddress(entity.getUserAddress());
        po.setUserAvatar(entity.getUserAvatar());
        po.setCreateTime(toEpochMilli(entity.getCreateTime()));
        po.setUpdateTime(toEpochMilli(entity.getUpdateTime()));
        return po;
    }

    private LocalDateTime toLocalDateTime(Long epochMilli) {
        if (epochMilli == null || epochMilli <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Long toEpochMilli(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
