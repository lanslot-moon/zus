package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.user.UserProfile;
import org.kitona.zus.domain.authorization.user.repository.IUserProfileRepository;
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

    /**
     * 创建 UserProfileRepositoryAdapter 实例。
     *
     * @param userInfoPersistenceRepository userInfoPersistenceRepository 参数
     */
    public UserProfileRepositoryAdapter(IUserInfoPersistenceRepository userInfoPersistenceRepository) {
        this.userInfoPersistenceRepository = userInfoPersistenceRepository;
    }

    /**
     * 按用户标识查询用户信息。
     *
     * @param userId 用户标识
     * @return 查询结果
     */
    @Override
    public Optional<UserProfile> findByUserId(String userId) {
        return userInfoPersistenceRepository.findByUserId(userId)
                .map(this::toEntity);
    }

    /**
     * 保存save。
     *
     * @param userInfo 用户信息
     */
    @Override
    public void save(UserProfile userInfo) {
        if (userInfo == null || StringUtils.isBlank(userInfo.getUserId())) {
            return;
        }
        userInfoPersistenceRepository.saveOrUpdate(toPO(userInfo));
    }

    /**
     * 转换为领域实体。
     *
     * @param po po 参数
     * @return 构建结果
     */
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

    /**
     * 转换为持久化对象。
     *
     * @param entity entity 参数
     * @return 构建结果
     */
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

    /**
     * 将毫秒时间戳转换为本地时间。
     *
     * @param epochMilli epochMilli 参数
     * @return 构建结果
     */
    private LocalDateTime toLocalDateTime(Long epochMilli) {
        if (epochMilli == null || epochMilli <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将本地时间转换为毫秒时间戳。
     *
     * @param dateTime dateTime 参数
     * @return 构建结果
     */
    private Long toEpochMilli(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
