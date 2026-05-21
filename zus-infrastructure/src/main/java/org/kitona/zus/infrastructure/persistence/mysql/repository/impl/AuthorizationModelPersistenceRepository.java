package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型持久化仓储实现（基础设施层，仅 fga_auth_model 单表）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class AuthorizationModelPersistenceRepository extends BaseRepository<AuthModelPO>
        implements IAuthorizationModelPersistenceRepository {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;

    @Resource
    private FgaCacheManager cacheManager;

    @Override
    public Optional<AuthModelPO> findByModelIdAndStoreId(String storeId, String modelId) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthModelPO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(modelId), AuthModelPO::getModelId, modelId);

        AuthModelPO model = this.getOne(wrapper);
        return Optional.ofNullable(model);
    }

    @Override
    public List<AuthModelPO> findByStoreId(String storeId) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthModelPO::getStoreId, storeId)
                .orderByDesc(AuthModelPO::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<AuthModelPO> findByStoreIdAndStatus(String storeId, Integer status) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), AuthModelPO::getStoreId, storeId)
                .eq(status != null, AuthModelPO::getStatus, status)
                .orderByDesc(AuthModelPO::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean updateModel(AuthModelPO model) {
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
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthModelPO::getStoreId, storeId)
                .eq(AuthModelPO::getModelId, modelId);

        AuthModelPO updatePO = new AuthModelPO();
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
    public Optional<AuthModelPO> findLatestByStoreId(String storeId) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthModelPO::getStoreId, storeId)
                .eq(AuthModelPO::getStatus, ModelPublishStatus.PUBLISHED.getStatus())
                .orderByDesc(AuthModelPO::getCreateTime);
        Page<AuthModelPO> page = new Page<>(1, 1, false);
        List<AuthModelPO> records = this.page(page, wrapper).getRecords();
        if (records.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(records.get(0));
    }

    @Override
    public void createModel(AuthModelPO model) {
        this.save(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDraftModel(String storeId, String modelId) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthModelPO::getStoreId, storeId)
                .eq(AuthModelPO::getModelId, modelId)
                .eq(AuthModelPO::getStatus, ModelPublishStatus.DRAFT.getStatus());

        AuthModelPO authorizationModelPO = new AuthModelPO();
        authorizationModelPO.setIsDeleted(DeletedStatusEnum.DELETED.getCode());
        boolean result = this.update(authorizationModelPO, wrapper);
        if (!result) {
            return false;
        }
        cacheManager.invalidateModel(storeId, modelId);
        log.info("删除草稿授权模型: storeId={}, modelId={}", storeId, modelId);
        return true;
    }

    @Override
    public List<AuthModelPO> findPageByCursor(String storeId, Integer status, String pageToken, int pageSize) {
        LambdaQueryWrapper<AuthModelPO> wrapper = getLambdaQueryWrapper()
                .eq(AuthModelPO::getStoreId, storeId)
                .eq(status != null, AuthModelPO::getStatus, status)
                .lt(StringUtils.isNotBlank(pageToken), AuthModelPO::getModelId, pageToken)
                .orderByDesc(AuthModelPO::getModelId);
        Page<AuthModelPO> page = new Page<>(1, resolvePageSize(pageSize), false);
        return this.page(page, wrapper).getRecords();
    }

    private int resolvePageSize(int pageSize) {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
