package org.kitona.zus.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 用户信息领域实体
 *
 * <p>封装用户的基本信息，作为领域层的实体对象。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Getter
@Builder
public class UserInfoEntity {

    private String userId;
    private String userName;
    private String userRole;
    private String userStatus;
    private String userPhone;
    private String userEmail;
    private String userAddress;
    private String userAvatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static UserInfoEntity create(String userId, String userName) {
        return UserInfoEntity.builder()
                .userId(userId)
                .userName(userName)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    public void updateEmail(String email) {
        this.userEmail = email;
        this.updateTime = LocalDateTime.now();
    }

    public void updateAddress(String address) {
        this.userAddress = address;
        this.updateTime = LocalDateTime.now();
    }
}
