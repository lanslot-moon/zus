package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IAuthorizationModelMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型持久化仓储实现（基础设施层，仅 fga_authorization_model 单表）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class AuthorizationModelPersistenceRepository extends BaseRepository<AuthorizationModelPO>
        implements IAuthorizationModelPersistenceRepository {

    @Resource
    private FgaCacheManager cacheManager;

    @Resource
    private IAuthorizationModelMapper authorizationModelMapper;

    @Override
    public Optional<AuthorizationModelPO> findByModelIdAndStoreId(String storeId, String modelId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthorizationModelPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), AuthorizationModelPO::getModelId, modelId);

        AuthorizationModelPO model = this.getOne(wrapper);
        return Optional.ofNullable(model);
    }

    @Override
    public List<AuthorizationModelPO> findByStoreId(String storeId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthorizationModelPO::getStoreId, storeId)
                .orderByDesc(AuthorizationModelPO::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<AuthorizationModelPO> findByStoreIdAndStatus(String storeId, Integer status) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthorizationModelPO::getStoreId, storeId)
                .eq(status != null, AuthorizationModelPO::getStatus, status)
                .orderByDesc(AuthorizationModelPO::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean updateModel(AuthorizationModelPO model) {
        if (model == null || model.getId() == null) {
            return false;
        }
        boolean result = this.updateById(model);
        if (result) {
            cacheManager.invalidateModel(model.getStoreId(), model.getModelId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateModelStatus(String storeId, String modelId, Integer status, String description) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthorizationModelPO::getStoreId, storeId)
                .eq(AuthorizationModelPO::getModelId, modelId);

        AuthorizationModelPO updatePO = new AuthorizationModelPO();
        updatePO.setStatus(status);
        updatePO.setDescription(description);

        boolean result = this.update(updatePO, wrapper);
        if (!result) {
            return false;
        }
        cacheManager.invalidateModel(storeId, modelId);
        log.info("更新授权模型状态: storeId={}, modelId={}, status={}", storeId, modelId, status);
        return true;
    }

    @Override
    public Optional<AuthorizationModelPO> findLatestByStoreId(String storeId) {
        AuthorizationModelPO model = authorizationModelMapper.selectLatestByStoreId(storeId);
        return Optional.ofNullable(model);
    }

    @Override
    public void createModel(AuthorizationModelPO model) {
        this.save(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishModel(String storeId, String modelId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthorizationModelPO::getStoreId, storeId)
                .eq(AuthorizationModelPO::getModelId, modelId);

        AuthorizationModelPO authorizationModelPO = new AuthorizationModelPO();
        authorizationModelPO.setStatus(ModelPublishStatus.PUBLISHED.getStatus());

        boolean result = this.update(authorizationModelPO, wrapper);
        if (!result) {
            return false;
        }
        cacheManager.invalidateModel(storeId, modelId);
        log.info("发布授权模型: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deprecateModel(String storeId, String modelId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthorizationModelPO::getStoreId, storeId)
                .eq(AuthorizationModelPO::getModelId, modelId);

        AuthorizationModelPO authorizationModelPO = new AuthorizationModelPO();
        authorizationModelPO.setStatus(ModelPublishStatus.ABANDONED.getStatus());
        boolean result = this.update(authorizationModelPO, wrapper);
        if (!result) {
            return false;
        }

        cacheManager.invalidateModel(storeId, modelId);
        log.info("废弃授权模型: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteModel(String storeId, String modelId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthorizationModelPO::getStoreId, storeId)
                .eq(AuthorizationModelPO::getModelId, modelId);

        AuthorizationModelPO authorizationModelPO = new AuthorizationModelPO();
        authorizationModelPO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());
        boolean result = this.update(authorizationModelPO, wrapper);
        if (!result) {
            return false;
        }
        cacheManager.invalidateModel(storeId, modelId);
        log.info("删除授权模型: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    public long countPublishedModels(String storeId) {
        LambdaQueryWrapper<AuthorizationModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthorizationModelPO::getStoreId, storeId);
        return this.count(wrapper);
    }

    @Override
    public List<AuthorizationModelPO> findPageByCursor(String storeId, Integer status, String pageToken, int pageSize) {
        return authorizationModelMapper.selectPageByCursor(storeId, status, pageToken, pageSize);
    }
}
