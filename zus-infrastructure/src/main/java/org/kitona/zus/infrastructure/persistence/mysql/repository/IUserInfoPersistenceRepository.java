package org.kitona.zus.infrastructure.persistence.mysql.repository;

import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/3/15 17:23
 * Version: V1.0
 * Description: Xxxx
 */public interface IUserInfoPersistenceRepository {
    /**
     * 根据用户ID获取用户信息对象
     *
     * @param userId 用户ID，用于标识唯一用户的字符串
     * @return UserInfoPO 包含用户信息的持久化对象，如果未找到则可能返回null
     */
    UserInfoPO getByUserId(String userId);
}
