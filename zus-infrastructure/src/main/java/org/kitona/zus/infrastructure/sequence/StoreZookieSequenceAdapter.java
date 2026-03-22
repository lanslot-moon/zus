package org.kitona.zus.infrastructure.sequence;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.port.IZookieSequencePort;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IStorePersistenceRepository;
import org.springframework.stereotype.Service;

/**
 * 基于 Store 持久化仓储的 Zookie 序列端口适配器。
 */
@Service
public class StoreZookieSequenceAdapter implements IZookieSequencePort {

    @Resource
    private IStorePersistenceRepository storePersistenceRepository;

    @Override
    public Long nextZookie(String storeId) {
        return storePersistenceRepository.nextZookie(storeId);
    }
}
