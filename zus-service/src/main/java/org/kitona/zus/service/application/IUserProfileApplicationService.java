package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.UserInfoDTO;

/**
 * 用户信息查询应用服务接口
 *
 * <p>提供用户信息只读查询能力。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IUserProfileApplicationService {

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息DTO
     */
    UserInfoDTO getUserInfo(String userId);
}
