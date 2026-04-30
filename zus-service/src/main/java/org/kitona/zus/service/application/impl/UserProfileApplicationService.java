package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.gateway.IUserInfoGateway;
import org.kitona.zus.service.exception.ApplicationException;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.domain.authorization.user.UserProfile;
import org.kitona.zus.domain.repository.IUserProfileRepository;
import org.kitona.zus.service.application.IUserProfileApplicationService;
import org.kitona.zus.service.conv.assembler.UserProfileAssembler;
import org.kitona.zus.service.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

/**
 * 用户信息查询应用服务实现
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>依赖 Domain 层的 {@link IUserProfileRepository} 接口</li>
 *   <li>通过领域实体 {@link UserProfile} 操作</li>
 *   <li>外部用户中心补充信息通过应用层网关 {@link IUserInfoGateway} 获取</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Service
public class UserProfileApplicationService implements IUserProfileApplicationService {

    @Resource
    private IUserProfileRepository userInfoDomainRepository;

    @Resource
    private IUserInfoGateway userInfoGateway;

    @Override
    public UserInfoDTO getUserInfo(String userId) {
        UserProfile userInfo = userInfoDomainRepository.findByUserId(userId).orElse(null);
        if (userInfo == null) {
            log.error("用户不存在, userId: {}", userId);
            throw new ApplicationException(IError.USER_NOT_EXIST);
        }

        String userAddress = userInfoGateway.getUserAddress(userId);
        if (StringUtils.isBlank(userAddress)) {
            log.error("用户地址不存在, userId: {}", userId);
            throw new ApplicationException(IError.USER_NOT_EXIST);
        }

        UserInfoDTO result = UserProfileAssembler.toDTO(userInfo, userAddress);
        log.info("查询用户信息成功, userId: {}, userInfo: {}", userId, JacksonUtil.toJSONString(result));
        return result;
    }
}
