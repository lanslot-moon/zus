package org.kitona.zus.domain.authorization.user.repository;

import org.kitona.zus.domain.authorization.user.UserProfile;

import java.util.Optional;

/**
 * 用户画像仓储接口。
 *
 * <p>用户画像属于授权上下文中的 user 子域，不属于 Store、AuthorizationModel、
 * RelationTuple 或 Changelog 这些授权核心聚合仓储。将接口放在 user 子域下，
 * 可以避免 root repository 包退化成所有持久化契约的杂物间。
 *
 * <p>Repository 只表达用户画像聚合/实体的装载与保存，不暴露 exists、update 字段、
 * 分页列表等查询或技术化能力。
 */
public interface IUserProfileRepository {

    /**
     * 根据用户 ID 查询用户画像。
     *
     * @param userId 用户唯一标识
     * @return 用户画像，不存在返回 empty
     */
    Optional<UserProfile> findByUserId(String userId);

    /**
     * 保存用户画像。
     *
     * @param userInfo 用户画像
     */
    void save(UserProfile userInfo);
}
