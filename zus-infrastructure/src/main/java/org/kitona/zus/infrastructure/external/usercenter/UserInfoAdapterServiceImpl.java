package org.kitona.zus.infrastructure.external.usercenter;

import org.kitona.zus.domain.gateway.IUserInfoGateway;
import org.springframework.stereotype.Service;

/**
 * 用户信息适配服务实现（防腐层）
 * 从外部用户中心获取用户信息，原 zus-facade 实现迁移至此。
 *
 * @author kitona
 */
@Service
public class UserInfoAdapterServiceImpl implements IUserInfoGateway {

    @Override
    public String getUserAddress(String userId) {
        // 进行三方系统的调用（如 Feign 调用用户中心）
        return "";
    }
}
