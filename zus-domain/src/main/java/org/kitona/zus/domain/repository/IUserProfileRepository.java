package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.user.UserProfile;

import java.util.Optional;

/**
 * 用户信息仓储接口（领域层）
 *
 * <p>定义用户信息的领域仓储契约，实现在基础设施层。
 *
 * <p>按照 DDD 规范，Repository 操作领域实体 {@link UserProfile}。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface IUserProfileRepository {

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户唯一标识
     * @return 用户信息实体，不存在返回 empty
     */
    Optional<UserProfile> findByUserId(String userId);

    /**
     * 保存用户信息（新增或更新）
     *
     * @param userInfo 用户信息实体
     * @return 保存成功返回 true
     */
    boolean save(UserProfile userInfo);

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 存在返回 true
     */
    boolean existsByUserId(String userId);
}
