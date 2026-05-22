package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.read.port.IAuthorizationModelQueryPort;
import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.domain.read.page.CursorPageResult;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型查询仓储适配器。
 */
@Repository
public class AuthorizationModelQueryRepositoryAdapter implements IAuthorizationModelQueryPort {

    @Resource
    private IAuthorizationModelPersistenceRepository authorizationModelPersistenceRepository;

    /**
     * 按游标分页查询读侧视图。
     *
     * @param storeId Store 标识
     * @param status 状态
     * @param pageToken 分页游标
     * @param pageSize 分页大小
     * @return 查询结果
     */
    @Override
    public CursorPageResult<AuthorizationModelView> findPageViewByCursor(String storeId, Integer status, String pageToken, int pageSize) {
        List<AuthModelPO> modelPOs = authorizationModelPersistenceRepository.findPageByCursor(storeId, status, pageToken, pageSize);
        if (CollectionUtils.isEmpty(modelPOs)) {
            return CursorPageResult.empty();
        }
        List<AuthorizationModelView> views = modelPOs.stream()
                .map(AuthorizationModelConverter::toView)
                .toList();
        String lastModelId = modelPOs.get(modelPOs.size() - 1).getModelId();
        return CursorPageResult.of(views, pageSize, lastModelId);
    }

    /**
     * 查询 Store 下最新发布模型读侧视图。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Optional<AuthorizationModelView> findLatestPublishedViewByStoreId(String storeId) {
        return authorizationModelPersistenceRepository.findLatestByStoreId(storeId).map(AuthorizationModelConverter::toView);
    }
}
