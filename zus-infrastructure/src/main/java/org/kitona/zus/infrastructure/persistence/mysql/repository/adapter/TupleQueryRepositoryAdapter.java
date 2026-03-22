package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.TupleConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleSubjectQuery;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ITuplePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关系元组查询仓储适配器。
 */
@Repository
public class TupleQueryRepositoryAdapter implements ITupleQueryRepository {

    @Resource
    private ITuplePersistenceRepository tupleRepository;

    @Override
    public List<RelationTupleEntity> listTuples(String storeId, String objectType, String relation, int pageSize, Long pageToken) {
        return TupleConverter.toEntityList(tupleRepository.listTuples(storeId, objectType, relation, pageSize, pageToken));
    }

    @Override
    public List<RelationTupleEntity> listTuplesWithFilter(String storeId, String objectType, String objectId,
                                                          String relation, String subjectType, String subjectId,
                                                          String subjectRelation, int pageSize, Long pageToken) {
        List<TuplePO> poList = tupleRepository.listTuplesWithFilter(storeId, objectType, objectId,
                relation, subjectType, subjectId, subjectRelation, pageSize, pageToken);
        return TupleConverter.toEntityList(poList);
    }

    @Override
    public List<RelationTupleEntity> findBySubject(String storeId, String subjectType, String subjectId, String subjectRelation,
                                                   String objectType, String relation, Long maxZookie) {
        TupleSubjectQuery query = TupleSubjectQuery.builder()
                .storeId(storeId)
                .subjectType(subjectType)
                .subjectId(subjectId)
                .subjectRelation(subjectRelation)
                .objectType(objectType)
                .relation(relation)
                .maxZookie(maxZookie)
                .build();
        return TupleConverter.toEntityList(tupleRepository.findBySubject(query));
    }

    @Override
    public List<RelationTupleEntity> findByObject(String storeId, String objectType, String objectId, String relation, Long maxZookie) {
        return TupleConverter.toEntityList(tupleRepository.findByObject(storeId, objectType, objectId, relation, maxZookie));
    }
}
