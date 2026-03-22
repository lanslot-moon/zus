package org.kitona.zus.domain.gateway;

/**
 * 用户信息外部查询网关
 *
 * <p>用于应用层在 DTO 组装或读侧补充时查询外部用户中心信息。
 * 该契约位于领域模块中以便被应用层引用、基础设施层实现，
 * 但不属于领域端口，也不参与领域规则和聚合不变量维护。
 */
public interface IUserInfoGateway {

    /**
     * 获取用户地址信息
     *
     * @param userId 用户ID
     * @return 用户地址，不存在或获取失败时返回空字符串
     */
    String getUserAddress(String userId);
}
