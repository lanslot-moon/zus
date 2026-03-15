package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IUserInfoMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IUserInfoPersistenceRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户信息持久化仓储实现（基础设施层）
 *
 * <p>
 * 基于 MyBatis-Plus 与 IUserInfoMapper 的持久化实现，
 * 供 UserInfoRepositoryDomainAdapter 适配为领域仓储。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Repository
public class UserInfoPersistenceRepository extends BaseRepository<UserInfoPO>
        implements IUserInfoPersistenceRepository {

    @Resource
    private IUserInfoMapper userInfoMapper;

    @Override
    public UserInfoPO getByUserId(String userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<UserInfoPO> wrapper = getLambdaQueryWrapper()
                .eq(UserInfoPO::getUserId, userId);
        return userInfoMapper.selectOne(wrapper);
    }
}
