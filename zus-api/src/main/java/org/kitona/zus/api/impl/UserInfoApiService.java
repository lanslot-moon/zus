package org.kitona.zus.api.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.entity.RestResult;
import org.kitona.zus.api.entity.params.UserInfoRequest;
import org.kitona.zus.api.verify.AddGroup;
import org.kitona.zus.api.entity.vo.UserInfoVO;
import org.kitona.zus.api.IUserInfoApiService;
import org.kitona.zus.business.entity.dto.UserInfoDTO;
import org.kitona.zus.business.service.IUserInfoBizService;
import org.kitona.zus.common.utils.OrikaUtils;
import org.kitona.zus.common.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserInfoApiService implements IUserInfoApiService {

    @Resource
    private IUserInfoBizService userInfoBizService;

    @Override
    public RestResult<UserInfoVO> getUserInfo(String userId) {
        log.info("UserInfoApiService getUserInfo,userId:{}", userId);
        UserInfoDTO userInfo = userInfoBizService.getUserInfo(userId);
        UserInfoVO convert = OrikaUtils.convert(userInfo, UserInfoVO.class);
        log.info("UserInfoApiService getUserInfo,userId:{}, userInfo:{}", userId, JSON.toJSONString(convert));
        return RestResult.success(convert);
    }

    @Override
    public RestResult<Boolean> deleteUserInfo(String userId) {
        return RestResult.success(false);
    }

    @Override
    public RestResult<Boolean> saveUserInfo(UserInfoRequest userInfoRequest) {
        log.info("UserInfoApiService saveUserInfo,userInfoRequest:{}", JSON.toJSONString(userInfoRequest));
        ValidationUtils.validate(userInfoRequest, AddGroup.class);
        return RestResult.success(false);
    }
}
