package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.read.port.IChangelogQueryPort;
import org.kitona.zus.domain.read.view.ChangelogView;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;
import org.kitona.zus.infrastructure.persistence.mysql.repository.IChangelogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 变更日志查询仓储适配器。
 */
@Repository
public class ChangelogQueryRepositoryAdapter implements IChangelogQueryPort {

    @Resource
    private IChangelogPersistenceRepository changelogPersistenceRepository;

    @Override
    public List<ChangelogView> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findByZookieRange(storeId, startZookie, endZookie, limit);
        return toViews(poList);
    }

    @Override
    public List<ChangelogView> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findAfterZookie(storeId, afterZookie, limit);
        return toViews(poList);
    }

    @Override
    public List<ChangelogView> findRecentChanges(String storeId, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findRecentChanges(storeId, limit);
        return toViews(poList);
    }

    private List<ChangelogView> toViews(List<TupleChangelogPO> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        return rows.stream().map(this::toView).toList();
    }

    private ChangelogView toView(TupleChangelogPO row) {
        return new ChangelogView(
                row.getZookie(),
                row.getOperation(),
                row.getObjectType(),
                row.getObjectId(),
                row.getRelation(),
                row.getSubjectType(),
                row.getSubjectId(),
                row.getSubjectRelation(),
                row.getOperatorId(),
                row.getRequestId(),
                row.getSource(),
                row.getOperationTime());
    }
}
