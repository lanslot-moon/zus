package org.kitona.zus.api;

import org.kitona.zus.api.entity.RestResult;
import org.kitona.zus.api.entity.params.UserInfoRequest;
import org.kitona.zus.api.entity.vo.UserInfoVO;
import org.springframework.web.bind.annotation.*;

@RestController
public interface IUserInfoApiService {

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping(value = "v1.0/userinfo/{userId}")
    RestResult<UserInfoVO> getUserInfo(@PathVariable("userId") String userId);

    /**
     * 删除用户信息
     * @param userId 用户id
     * @return 删除结果
     */
    @DeleteMapping("v1.0/userinfo/{userId}")
    RestResult<Boolean> deleteUserInfo(@PathVariable("userId") String userId);

    /**
     * 保存用户信息
     * @param userInfoRequest 用户信息
     * @return 保存结果
     */
    @PostMapping("v1.0/userinfo")
    RestResult<Boolean> saveUserInfo(@RequestBody UserInfoRequest userInfoRequest);
}
