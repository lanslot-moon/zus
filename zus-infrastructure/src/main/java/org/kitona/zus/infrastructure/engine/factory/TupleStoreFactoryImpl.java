package org.kitona.zus.infrastructure.engine.factory;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.port.ITupleStore;
import org.kitona.zus.domain.port.ITupleStoreFactory;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.infrastructure.engine.tuple.RepositoryTupleStore;
import org.springframework.stereotype.Component;

/**
 * 元组存储工厂实现
 *
 * <p>创建基于仓储的 {@link ITupleStore} 实例，用于生产环境的权限检查。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Component
public class TupleStoreFactoryImpl implements ITupleStoreFactory {

    @Resource
    private ITupleDomainRepository tupleRepository;

    @Override
    public ITupleStore create(String storeId, Zookie zookie) {
        Long maxZookie = (zookie != null) ? zookie.getVersion() : null;
        return new RepositoryTupleStore(tupleRepository, storeId, maxZookie);
    }
}
