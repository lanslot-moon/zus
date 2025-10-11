package org.kitona.zus.business.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoDTO implements Serializable {

    private String login;
    private String name;
    private String email;
    private String role;

    private String address;
}
