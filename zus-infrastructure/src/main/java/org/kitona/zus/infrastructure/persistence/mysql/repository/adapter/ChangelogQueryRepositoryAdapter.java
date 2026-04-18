package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.repository.IChangelogQueryRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.ChangelogConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 变更日志查询仓储适配器。
 */
@Repository
public class ChangelogQueryRepositoryAdapter implements IChangelogQueryRepository {

    private final IChangelogPersistenceRepository changelogPersistenceRepository;

    public ChangelogQueryRepositoryAdapter(IChangelogPersistenceRepository changelogPersistenceRepository) {
        this.changelogPersistenceRepository = changelogPersistenceRepository;
    }

    @Override
    public List<Changelog> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findByZookieRange(storeId, startZookie, endZookie, limit);
        return ChangelogConverter.toEntityList(poList);
    }

    @Override
    public List<Changelog> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findAfterZookie(storeId, afterZookie, limit);
        return ChangelogConverter.toEntityList(poList);
    }

    @Override
    public Long getMaxZookie(String storeId) {
        return changelogPersistenceRepository.getMaxZookie(storeId);
    }

    @Override
    public List<Changelog> findRecentChanges(String storeId, Integer limit) {
        List<ChangelogPO> poList = changelogPersistenceRepository.findRecentChanges(storeId, limit);
        return ChangelogConverter.toEntityList(poList);
    }
}
