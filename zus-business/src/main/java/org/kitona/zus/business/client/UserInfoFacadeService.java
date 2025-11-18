package org.kitona.zus.business.client;

import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.client.entity.Result;
import org.kitona.zus.client.entity.dto.UserInfoFacadeDto;
import org.kitona.zus.client.service.IUserInfoFacade;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.domain.user.IUserInfoService;
import org.kitona.zus.service.domain.user.entity.bo.UserInfoBO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class UserInfoFacadeService implements IUserInfoFacade {

    @Resource
    private IUserInfoService userInfoService;

    @Override
    public Result<UserInfoFacadeDto> getUserInfo(String userId) {
        UserInfoBO userInfo = userInfoService.getUserInfo(userId);
        if (userInfo == null) {
            log.error("UserInfoFacadeService getUserInfo is null: {}", userId);
            return Result.error(IError.USER_NOT_EXIST);
        }

        UserInfoFacadeDto convert = MapstructUtil.convert(userInfo, UserInfoFacadeDto.class);
        log.info("UserInfoFacadeService getUserInfo result is:{}", JacksonUtil.toJSONString(convert));
        return Result.success(convert);
    }
}
