package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.StoreConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IStorePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 存储空间仓储领域接口适配器
 *
 * <p>实现 domain 层 IStoreDomainRepository，委托基础设施层仓储并做 PO↔聚合根转换。
 */
@Repository
public class StoreDomainRepositoryAdapter implements IStoreDomainRepository {

    @Resource
    private IStorePersistenceRepository storeRepository;

    /**
     * 按聚合标识查询聚合根。
     *
     * @param storeId Store 标识
     * @return 查询结果
     */
    @Override
    public Optional<StoreAggregate> findById(String storeId) {
        return storeRepository.findByStoreId(storeId).map(StoreConverter::toAggregate);
    }

    /**
     * 保存save。
     *
     * @param store Store 聚合
     */
    @Override
    public void save(StoreAggregate store) {
        StorePO po = StoreConverter.toPO(store);
        if (store.getId() == null) {
            storeRepository.createStore(po);
            return;
        }
        storeRepository.updateStore(po);
    }

    /**
     * 移除remove。
     *
     * @param store Store 聚合
     */
    @Override
    public void remove(StoreAggregate store) {
        if (store != null) {
            storeRepository.deleteByStoreId(store.getStoreId());
        }
    }
}
