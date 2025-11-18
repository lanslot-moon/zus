package org.kitona.zus.service.domain.user.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoBO implements Serializable {

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
