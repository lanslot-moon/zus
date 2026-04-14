package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.ChangelogConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 变更日志仓储领域接口适配器。
 */
@Repository
public class ChangelogDomainRepositoryAdapter implements IChangelogDomainRepository {

    @Resource
    private IChangelogPersistenceRepository changelogPersistenceRepository;

    @Override
    public void saveBatch(List<Changelog> changelogs) {
        if (changelogs == null || changelogs.isEmpty()) {
            return;
        }
        List<ChangelogPO> poList = ChangelogConverter.toPOList(changelogs);
        changelogPersistenceRepository.batchCreate(poList);
    }

    @Override
    public void save(Changelog changelog) {
        if (changelog == null) {
            return;
        }
        changelogPersistenceRepository.batchCreate(List.of(ChangelogConverter.toPO(changelog)));
    }

    @Override
    public int deleteBeforeZookie(String storeId, Long beforeZookie) {
        return changelogPersistenceRepository.cleanupBeforeZookie(storeId, beforeZookie);
    }
}
