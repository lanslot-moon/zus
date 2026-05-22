package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.read.port.IZookieWatermarkQueryPort;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;

/**
 * Zookie 读侧水位查询适配器。
 *
 * <p>一致性水位当前来源于 tuple changelog 的最大 zookie，但这个实现细节不应该泄漏到
 * {@link org.kitona.zus.domain.read.port.IChangelogQueryPort} 的列表查询语义中。
 */
@Repository
public class ZookieWatermarkQueryRepositoryAdapter implements IZookieWatermarkQueryPort {

    /**
     * changelog 技术持久化仓储。
     */
    @Resource
    private IChangelogPersistenceRepository changelogPersistenceRepository;


    @Override
    public Long currentMaxZookie(String storeId) {
        return changelogPersistenceRepository.getMaxZookie(storeId);
    }
}
