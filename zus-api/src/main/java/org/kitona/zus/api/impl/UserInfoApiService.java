package org.kitona.zus.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.entity.RestResult;
import org.kitona.zus.api.entity.request.UserInfoApiRequest;
import org.kitona.zus.api.verify.AddGroup;
import org.kitona.zus.api.entity.vo.UserInfoVO;
import org.kitona.zus.api.IUserInfoApiService;
import org.kitona.zus.business.entity.dto.UserInfoDTO;
import org.kitona.zus.business.service.IUserInfoBizService;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Slf4j
@Service
public class UserInfoApiService implements IUserInfoApiService {

    @Resource
    private IUserInfoBizService userInfoBizService;

    @Override
    public RestResult<UserInfoVO> getUserInfo(String userId) {
        log.info("UserInfoApiService getUserInfo,userId:{}", userId);
        UserInfoDTO userInfo = userInfoBizService.getUserInfo(userId);
        UserInfoVO convert = MapstructUtil.convert(userInfo, UserInfoVO.class);
        log.info("UserInfoApiService getUserInfo,userId:{}, userInfo:{}", userId, JacksonUtil.toJSONString(convert));
        return RestResult.success(convert);
    }

    @Override
    public RestResult<Boolean> deleteUserInfo(String userId) {
        return RestResult.success(false);
    }

    @Override
    public RestResult<Boolean> saveUserInfo(UserInfoApiRequest userInfoApiRequest) {
        log.info("UserInfoApiService saveUserInfo,userInfoRequest:{}", JacksonUtil.toJSONString(userInfoApiRequest));
        ValidationUtil.validate(userInfoApiRequest, AddGroup.class);
        return RestResult.success(false);
    }
}
