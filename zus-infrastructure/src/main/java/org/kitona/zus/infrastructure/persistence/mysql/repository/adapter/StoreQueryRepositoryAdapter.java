package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.read.page.CursorPageResult;
import org.kitona.zus.infrastructure.persistence.mysql.converter.StoreConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IStorePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Store 查询仓储适配器。
 */
@Repository
public class StoreQueryRepositoryAdapter implements IStoreQueryPort {

    @Resource
    private IStorePersistenceRepository storePersistenceRepository;


    /**
     * 按 Store 标识查询读侧视图。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Optional<StoreView> findViewByStoreId(String storeId) {
        return storePersistenceRepository.findByStoreId(storeId)
                .map(StoreConverter::toView);
    }

    /**
     * 按游标分页查询读侧视图。
     *
     * @param pageToken 分页游标
     * @param pageSize 分页大小
     * @return 查询结果
     */
    @Override
    public CursorPageResult<StoreView> findPageViewByCursor(String pageToken, int pageSize) {
        List<StorePO> storePOS = storePersistenceRepository.findPageByCursor(pageToken, pageSize);
        if (CollectionUtils.isEmpty(storePOS)) {
            return CursorPageResult.empty();
        }
        List<StoreView> views = storePOS.stream()
                .map(StoreConverter::toView)
                .toList();
        String lastStoreId = storePOS.get(storePOS.size() - 1).getStoreId();
        return CursorPageResult.of(views, pageSize, lastStoreId);
    }
}
