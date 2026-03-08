package org.kitona.zus.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;
    private String userPassword;
    private String userRole;
    private String userStatus;
    private String userPhone;
    private String userEmail;
    private String userAddress;
    private String userAvatar;
    private String userCreateTime;
    private String userUpdateTime;
}
