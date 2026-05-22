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

    /**
     * 按 zookie 范围查询变更日志。
     *
     * @param storeId Store 标识
     * @param startZookie startZookie 参数
     * @param endZookie endZookie 参数
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<ChangelogView> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findByZookieRange(storeId, startZookie, endZookie, limit);
        return toViews(poList);
    }

    /**
     * 查询指定 zookie 之后的变更日志。
     *
     * @param storeId Store 标识
     * @param afterZookie afterZookie 参数
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<ChangelogView> findAfterZookie(String storeId, Long afterZookie, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findAfterZookie(storeId, afterZookie, limit);
        return toViews(poList);
    }

    /**
     * 查询最近的变更日志。
     *
     * @param storeId Store 标识
     * @param limit limit 参数
     * @return 查询结果
     */
    @Override
    public List<ChangelogView> findRecentChanges(String storeId, Integer limit) {
        List<TupleChangelogPO> poList = changelogPersistenceRepository.findRecentChanges(storeId, limit);
        return toViews(poList);
    }

    /**
     * 批量转换为读侧视图。
     *
     * @param rows rows 参数
     * @return 构建结果
     */
    private List<ChangelogView> toViews(List<TupleChangelogPO> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        return rows.stream().map(this::toView).toList();
    }

    /**
     * 转换为读侧视图。
     *
     * @param row row 参数
     * @return 构建结果
     */
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
