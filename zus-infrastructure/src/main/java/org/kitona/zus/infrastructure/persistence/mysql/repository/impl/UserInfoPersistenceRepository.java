package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.infrastructure.persistence.mysql.entity.UserInfoPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IUserInfoMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IUserInfoPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户信息持久化仓储实现（基础设施层）
 *
 * <p>
 * 基于 MyBatis-Plus 与 IUserInfoMapper 的持久化实现，
 * 供 UserProfileRepositoryAdapter 适配为领域仓储。
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

    /**
     * 按用户标识查询用户信息。
     *
     * @param userId 用户标识
     * @return 查询结果
     */
    @Override
    public Optional<UserInfoPO> findByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Optional.empty();
        }
        LambdaQueryWrapper<UserInfoPO> wrapper = getLambdaQueryWrapper()
                .eq(UserInfoPO::getUserId, userId);
        return Optional.ofNullable(userInfoMapper.selectOne(wrapper));
    }

    /**
     * 保存save or update。
     *
     * @param userInfo 用户信息
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean saveOrUpdate(UserInfoPO userInfo) {
        if (userInfo == null || StringUtils.isBlank(userInfo.getUserId())) {
            return false;
        }
        Optional<UserInfoPO> existing = findByUserId(userInfo.getUserId());
        if (existing.isEmpty()) {
            return this.save(userInfo);
        }
        LambdaUpdateWrapper<UserInfoPO> wrapper = new LambdaUpdateWrapper<UserInfoPO>()
                .eq(UserInfoPO::getUserId, userInfo.getUserId());
        return this.update(userInfo, wrapper);
    }

    /**
     * 判断用户信息是否存在。
     *
     * @param userId 用户标识
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean existsByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        LambdaQueryWrapper<UserInfoPO> wrapper = getLambdaQueryWrapper()
                .eq(UserInfoPO::getUserId, userId);
        return this.count(wrapper) > 0;
    }
}
