package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.ChangelogConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;
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

    /**
     * 保存save all。
     *
     * @param changelogs 变更日志列表
     */
    @Override
    public void saveAll(List<Changelog> changelogs) {
        if (changelogs == null || changelogs.isEmpty()) {
            return;
        }
        List<TupleChangelogPO> poList = ChangelogConverter.toPOList(changelogs);
        changelogPersistenceRepository.batchCreate(poList);
    }

    /**
     * 保存save。
     *
     * @param changelog 变更日志
     */
    @Override
    public void save(Changelog changelog) {
        if (changelog == null) {
            return;
        }
        changelogPersistenceRepository.batchCreate(List.of(ChangelogConverter.toPO(changelog)));
    }
}
