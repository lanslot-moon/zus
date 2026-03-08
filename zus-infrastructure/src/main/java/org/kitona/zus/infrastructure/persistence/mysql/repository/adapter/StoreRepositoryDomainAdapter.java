package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.domain.enums.StoreStatus;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.StoreConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.impl.StorePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 存储空间仓储领域接口适配器
 *
 * 实现 domain 层 IStoreRepository，委托基础设施层仓储并做 PO↔聚合根 转换。
 * 按照 DDD 规范，Repository 操作聚合根 {@link StoreAggregate}。
 *
 * @author kitona
 */
@Repository
public class StoreRepositoryDomainAdapter implements IStoreDomainRepository {

    @Resource
    private StorePersistenceRepository storeRepository;

    @Override
    public Optional<StoreAggregate> findByStoreId(String storeId) {
        return storeRepository.findByStoreId(storeId).map(StoreConverter::toAggregate);
    }

    @Override
    public List<StoreAggregate> findByStatus(StoreStatus status) {
        Integer statusCode = status != null ? status.getCode() : null;
        return storeRepository.findByStatus(statusCode).stream()
                .map(StoreConverter::toAggregate)
                .toList();
    }

    @Override
    public boolean saveOrUpdateStore(StoreAggregate store) {
        StorePO po = StoreConverter.toPO(store);
        if (store.getId() == null) {
            return storeRepository.createStore(po);
        }
        return storeRepository.updateStore(po);
    }

    @Override
    public boolean updateCurrentModelId(String storeId, String modelId) {
        return storeRepository.updateCurrentModelId(storeId, modelId);
    }

    @Override
    public Long nextZookie(String storeId) {
        return storeRepository.nextZookie(storeId);
    }

    @Override
    public Long getCurrentZookie(String storeId) {
        return storeRepository.getCurrentZookie(storeId);
    }

    @Override
    public boolean deleteByStoreId(String storeId) {
        return storeRepository.deleteByStoreId(storeId);
    }

    @Override
    public boolean existsByStoreId(String storeId) {
        return storeRepository.existsByStoreId(storeId);
    }
}
