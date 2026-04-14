package org.kitona.zus.api.facade;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.client.entity.Result;
import org.kitona.zus.client.entity.dto.UserInfoFacadeDto;
import org.kitona.zus.client.service.IUserInfoFacade;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IUserProfileApplicationService;
import org.kitona.zus.service.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

/**
 * 用户信息 Facade 实现
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>Facade 位于用户接口层，作为对外 RPC 接口的实现</li>
 *   <li>调用应用服务完成业务逻辑，不直接依赖领域层</li>
 *   <li>负责 DTO 转换、异常包装等接口适配工作</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Service
public class UserInfoFacadeImpl implements IUserInfoFacade {

    @Resource
    private IUserProfileApplicationService userInfoApplicationService;

    @Override
    public Result<UserInfoFacadeDto> getUserInfo(String userId) {
        try {
            UserInfoDTO userInfo = userInfoApplicationService.getUserInfo(userId);
            if (userInfo == null) {
                log.warn("UserInfoFacadeImpl.getUserInfo 用户不存在: userId={}", userId);
                return Result.error(IError.USER_NOT_EXIST);
            }

            UserInfoFacadeDto facadeDto = MapstructUtil.convert(userInfo, UserInfoFacadeDto.class);
            log.info("UserInfoFacadeImpl.getUserInfo 查询成功: userId={}, result={}", userId, JacksonUtil.toJSONString(facadeDto));
            return Result.success(facadeDto);
        } catch (Exception e) {
            log.error("UserInfoFacadeImpl.getUserInfo 查询异常: userId={}", userId, e);
            return Result.error(IError.SYSTEM_ERROR);
        }
    }
}
