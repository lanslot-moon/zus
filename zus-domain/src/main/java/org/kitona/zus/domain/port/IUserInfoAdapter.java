package org.kitona.zus.domain.port;

/**
 * 用户信息适配器端口（防腐层）
 * 
 * <p>用于隔离外部用户中心系统的依赖。
 * 防腐层是一种边界保护机制，确保：
 * <ul>
 *   <li>领域模型不被外部系统的数据结构污染</li>
 *   <li>外部系统变更不会直接影响核心领域逻辑</li>
 *   <li>便于测试，可通过 Mock 实现替换外部依赖</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IUserInfoAdapter {

    /**
     * 获取用户地址信息
     * 
     * <p>从外部用户中心获取用户的地址信息。
     * 如果用户不存在或外部系统异常，返回空字符串而不是抛出异常。
     *
     * @param userId 用户ID
     * @return 用户地址，不存在或获取失败时返回空字符串
     */
    String getUserAddress(String userId);
}
