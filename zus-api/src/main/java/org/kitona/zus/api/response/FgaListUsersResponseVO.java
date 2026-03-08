package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA ListUsers API 响应
 */
@Data
public class FgaListUsersResponseVO {

    /**
     * 对资源有权限的用户列表
     */
    private List<FgaUserVO> users;
}
