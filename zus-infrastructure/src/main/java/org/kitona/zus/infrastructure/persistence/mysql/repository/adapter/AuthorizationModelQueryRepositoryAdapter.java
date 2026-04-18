package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.domain.repository.IAuthorizationModelQueryRepository;
import org.kitona.zus.domain.valueobject.CursorPageResult;
import org.kitona.zus.infrastructure.persistence.mysql.converter.AuthorizationModelConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthorizationModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IAuthorizationModelPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型查询仓储适配器。
 */
@Repository
public class AuthorizationModelQueryRepositoryAdapter implements IAuthorizationModelQueryRepository {

    private final IAuthorizationModelPersistenceRepository authorizationModelPersistenceRepository;

    public AuthorizationModelQueryRepositoryAdapter(
            IAuthorizationModelPersistenceRepository authorizationModelPersistenceRepository) {
        this.authorizationModelPersistenceRepository = authorizationModelPersistenceRepository;
    }

    @Override
    public CursorPageResult<AuthorizationModelView> findPageViewByCursor(String storeId, Integer status, String pageToken, int pageSize) {
        List<AuthorizationModelPO> modelPOs = authorizationModelPersistenceRepository.findPageByCursor(storeId, status, pageToken, pageSize);
        if (CollectionUtils.isEmpty(modelPOs)) {
            return CursorPageResult.empty();
        }
        List<AuthorizationModelView> views = modelPOs.stream()
                .map(AuthorizationModelConverter::toView)
                .toList();
        String lastModelId = modelPOs.get(modelPOs.size() - 1).getModelId();
        return CursorPageResult.of(views, pageSize, lastModelId);
    }

    @Override
    public Optional<AuthorizationModelView> findLatestPublishedViewByStoreId(String storeId) {
        return authorizationModelPersistenceRepository.findLatestByStoreId(storeId)
                .map(AuthorizationModelConverter::toView);
    }
}
