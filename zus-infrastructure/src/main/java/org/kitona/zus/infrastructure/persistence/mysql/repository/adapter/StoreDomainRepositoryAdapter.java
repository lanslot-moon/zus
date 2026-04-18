package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.domain.enums.StoreStatus;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.StoreConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IStorePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 存储空间仓储领域接口适配器
 *
 * <p>实现 domain 层 IStoreDomainRepository，委托基础设施层仓储并做 PO↔聚合根转换。
 */
@Repository
public class StoreDomainRepositoryAdapter implements IStoreDomainRepository {

    private final IStorePersistenceRepository storeRepository;

    public StoreDomainRepositoryAdapter(IStorePersistenceRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

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
    public boolean deleteByStoreId(String storeId) {
        return storeRepository.deleteByStoreId(storeId);
    }

}
