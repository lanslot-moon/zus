package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.entity.ChangelogEntity;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.ChangelogConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 变更日志仓储领域接口适配器
 *
 * <p>
 * 实现领域层定义的 {@link IChangelogDomainRepository} 接口。
 * 委托持久化仓储 {@link IChangelogPersistenceRepository} 完成真实的数据操作，
 * 并处理领域实体与持久化对象（PO）之间的转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-03-15
 */
@Repository
public class ChangelogRepositoryDomainAdapter implements IChangelogDomainRepository {

    @Resource
    private IChangelogPersistenceRepository changelogPersistenceRepository;

    @Override
    public void saveBatch(List<ChangelogEntity> changelogs) {
        if (changelogs == null || changelogs.isEmpty()) {
            return;
        }
        List<ChangelogPO> poList = ChangelogConverter.toPOList(changelogs);
        changelogPersistenceRepository.saveBatch(poList);
    }

    @Override
    public void save(ChangelogEntity changelog) {
        if (changelog == null) {
            return;
        }
        changelogPersistenceRepository.save(ChangelogConverter.toPO(changelog));
    }

    @Override
    public List<ChangelogEntity> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findByZookieRange(storeId, startZookie, endZookie,
                limit);
        return ChangelogConverter.toEntityList(poList);
    }

    @Override
    public List<ChangelogEntity> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findAfterZookie(storeId, afterZookie, limit);
        return ChangelogConverter.toEntityList(poList);
    }

    @Override
    public Long getMaxZookie(String storeId) {
        return changelogPersistenceRepository.getMaxZookie(storeId);
    }

    @Override
    public List<ChangelogEntity> findRecentChanges(String storeId, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findRecentChanges(storeId, limit);
        return ChangelogConverter.toEntityList(poList);
    }

    @Override
    public int deleteBeforeZookie(String storeId, Long beforeZookie) {
        return changelogPersistenceRepository.cleanupBeforeZookie(storeId, beforeZookie);
    }
}
