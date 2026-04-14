package org.kitona.zus.common.gateway;

/**
 * 用户信息外部查询网关。
 *
 * <p>该契约服务于应用层 DTO 组装与读侧补充，
 * 不属于领域规则或聚合不变量的一部分。
 */
public interface IUserInfoGateway {

    /**
     * 获取用户地址信息。
     *
     * @param userId 用户ID
     * @return 用户地址，不存在或获取失败时返回空字符串
     */
    String getUserAddress(String userId);
}
