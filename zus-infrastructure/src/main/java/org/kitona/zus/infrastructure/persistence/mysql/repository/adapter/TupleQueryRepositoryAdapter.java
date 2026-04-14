package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.domain.valueobject.ObjectRef;
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
public class TupleQueryRepositoryAdapter implements ITupleQueryRepository, IDirectTupleReader,
        ITupleLinkReader, ISubjectObjectCandidateReader, IObjectSubjectCandidateReader {

    @Resource
    private ITuplePersistenceRepository tupleRepository;

    @Override
    public List<RelationTuple> list(TupleQueryCriteria criteria) {
        List<TuplePO> poList = tupleRepository.listTuplesWithFilter(
                criteria.storeId(),
                criteria.objectType(),
                criteria.objectId(),
                criteria.relation(),
                criteria.subjectType(),
                criteria.subjectId(),
                criteria.subjectRelation(),
                criteria.effectivePageSize(Integer.MAX_VALUE),
                criteria.pageToken(),
                criteria.maxZookie()
        );
        return TupleConverter.toEntityList(poList);
    }

    @Override
    public List<RelationTuple> findBySubject(TupleQueryCriteria criteria) {
        TupleSubjectQuery query = TupleSubjectQuery.builder()
                .storeId(criteria.storeId())
                .subjectType(criteria.subjectType())
                .subjectId(criteria.subjectId())
                .subjectRelation(criteria.subjectRelation())
                .objectType(criteria.objectType())
                .relation(criteria.relation())
                .maxZookie(criteria.maxZookie())
                .build();
        return TupleConverter.toEntityList(tupleRepository.findBySubject(query));
    }

    @Override
    public List<RelationTuple> findByObject(TupleQueryCriteria criteria) {
        return TupleConverter.toEntityList(tupleRepository.findByObject(
                criteria.storeId(),
                criteria.objectType(),
                criteria.objectId(),
                criteria.relation(),
                criteria.maxZookie()));
    }

    @Override
    public List<RelationTuple> findDirectTuples(String storeId, ObjectRef object, String relation, Long maxZookie) {
        return findByObject(TupleQueryCriteria.forObject(storeId, object.getType(), object.getId(), relation, maxZookie));
    }

    @Override
    public List<RelationTuple> findTupleLinks(String storeId, ObjectRef object, String relation, Long maxZookie) {
        return findByObject(TupleQueryCriteria.forObject(storeId, object.getType(), object.getId(), relation, maxZookie));
    }

    @Override
    public List<RelationTuple> listObjectCandidates(String storeId, String objectType, Long maxZookie) {
        return list(TupleQueryCriteria.forPage(storeId, objectType, null, null,
                null, null, null, Integer.MAX_VALUE, null, maxZookie));
    }

    @Override
    public List<RelationTuple> listSubjectCandidates(String storeId, Long maxZookie) {
        return list(TupleQueryCriteria.forPage(storeId, null, null, null,
                null, null, null, Integer.MAX_VALUE, null, maxZookie));
    }
}
