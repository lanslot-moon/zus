package org.kitona.zus.business.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.business.entity.dto.UserInfoDTO;
import org.kitona.zus.business.service.IUserInfoBizService;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.RestException;
import org.kitona.zus.common.utils.OrikaUtils;
import org.kitona.zus.facade.service.IUserInfoAdapterService;
import org.kitona.zus.service.IUserInfoService;
import org.kitona.zus.service.entity.bo.UserInfoBO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class UserInfoBizService implements IUserInfoBizService {

    @Resource
    private IUserInfoService userInfoService;

    @Resource
    private IUserInfoAdapterService userInfoAdapterService;

    @Override
    public UserInfoDTO getUserInfo(String userId) {
        UserInfoBO userInfo = userInfoService.getUserInfo(userId);
        if (userInfo == null) {
            log.error("UserInfoBizService getUserInfo userInfo is null, userId:{}", userId);
            throw new RestException(IError.USER_NOT_EXIST);
        }

        String userAddress = userInfoAdapterService.getUserAddress(userId);
        if (StringUtils.isBlank(userAddress)) {
            log.error("UserInfoBizService getUserInfo userAddress is null, userId:{}", userId);
            throw new RestException(IError.USER_NOT_EXIST);
        }

        UserInfoDTO convert = OrikaUtils.convert(userInfo, UserInfoDTO.class);
        convert.setEmail(userAddress);
        log.info("UserInfoBizService getUserInfo success, userId:{}, userInfo:{}", userId, JSON.toJSONString(convert));
        return convert;
    }
}
