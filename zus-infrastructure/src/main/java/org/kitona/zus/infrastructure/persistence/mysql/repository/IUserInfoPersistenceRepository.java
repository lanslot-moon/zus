package org.kitona.zus.infrastructure.persistence.mysql.repository;

import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;

import java.util.Optional;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/3/15 17:23
 * Version: V1.0
 * Description: Xxxx
 */
public interface IUserInfoPersistenceRepository {
    /**
     * 根据用户ID获取用户信息对象
     *
     * @param userId 用户ID，用于标识唯一用户的字符串
     * @return UserInfoPO 包含用户信息的持久化对象，如果未找到则可能返回null
     */
    Optional<UserInfoPO> findByUserId(String userId);

    /**
     * 保存或更新用户信息
     *
     * @param userInfo 用户信息持久化对象
     * @return 保存成功返回 true
     */
    boolean saveOrUpdate(UserInfoPO userInfo);

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 存在返回 true
     */
    boolean existsByUserId(String userId);
}
