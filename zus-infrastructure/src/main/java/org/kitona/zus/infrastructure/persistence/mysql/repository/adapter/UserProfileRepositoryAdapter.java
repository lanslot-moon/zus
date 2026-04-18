package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.user.UserProfile;
import org.kitona.zus.domain.repository.IUserProfileRepository;
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
public class UserProfileRepositoryAdapter implements IUserProfileRepository {

    private final IUserInfoPersistenceRepository userInfoPersistenceRepository;

    public UserProfileRepositoryAdapter(IUserInfoPersistenceRepository userInfoPersistenceRepository) {
        this.userInfoPersistenceRepository = userInfoPersistenceRepository;
    }

    @Override
    public Optional<UserProfile> findByUserId(String userId) {
        return userInfoPersistenceRepository.findByUserId(userId)
                .map(this::toEntity);
    }

    @Override
    public boolean save(UserProfile userInfo) {
        if (userInfo == null || StringUtils.isBlank(userInfo.getUserId())) {
            return false;
        }
        return userInfoPersistenceRepository.saveOrUpdate(toPO(userInfo));
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userInfoPersistenceRepository.existsByUserId(userId);
    }

    private UserProfile toEntity(UserInfoPO po) {
        if (po == null) {
            return null;
        }
        return UserProfile.builder()
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

    private UserInfoPO toPO(UserProfile entity) {
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
