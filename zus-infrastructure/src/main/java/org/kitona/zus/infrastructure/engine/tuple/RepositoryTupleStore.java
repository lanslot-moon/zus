package org.kitona.zus.infrastructure.engine.tuple;

import lombok.RequiredArgsConstructor;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.port.ITupleStore;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.valueobject.RelationTuple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于仓储的元组存储实现
 *
 * <p>通过领域仓储接口查询元组，用于生产环境的权限检查。
 * 每个实例绑定特定的 storeId 和 zookie（一致性令牌）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@RequiredArgsConstructor
public class RepositoryTupleStore implements ITupleStore {

    private final ITupleDomainRepository tupleRepository;
    private final String storeId;
    private final Long maxZookie;

    @Override
    public boolean hasTuple(String subjectType, String subjectId, String resourceType,
                            String resourceId, String relationName) {
        return tupleRepository.existsTuple(storeId, resourceType, resourceId, relationName,
                subjectType, subjectId, null, maxZookie);
    }

    @Override
    public Set<RelationTuple> findTuples(String resourceType, String resourceId, String relationName) {
        List<RelationTupleEntity> entities = tupleRepository.findByObjectAndRelation(
                storeId, resourceType, resourceId, relationName, maxZookie);

        Set<RelationTuple> result = new HashSet<>();
        for (RelationTupleEntity entity : entities) {
            result.add(new RelationTuple(
                    entity.getSubjectType(),
                    entity.getSubjectId(),
                    entity.getObjectType(),
                    entity.getObjectId(),
                    entity.getRelation()
            ));
        }
        return result;
    }
}
