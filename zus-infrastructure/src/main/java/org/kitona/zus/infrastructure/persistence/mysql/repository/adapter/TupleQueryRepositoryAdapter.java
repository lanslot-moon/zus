package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.read.view.TupleView;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.infrastructure.persistence.mysql.converter.TupleConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleSubjectQuery;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ITuplePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关系元组查询仓储适配器。
 */
@Repository
public class TupleQueryRepositoryAdapter implements ITupleQueryPort, IDirectTupleReader,
        ITupleLinkReader, ISubjectObjectCandidateReader, IObjectSubjectCandidateReader {

    private static final int UNBOUNDED_PAGE_SIZE = Integer.MAX_VALUE;

    @Resource
    private ITuplePersistenceRepository tupleRepository;


    @Override
    public List<TupleView> list(TupleQueryCriteria criteria) {
        return toViews(listTupleRows(criteria));
    }

    @Override
    public List<TupleView> findBySubject(TupleQueryCriteria criteria) {
        TupleSubjectQuery query = TupleSubjectQuery.builder()
                .storeId(criteria.storeId())
                .subjectType(criteria.subjectType())
                .subjectId(criteria.subjectId())
                .subjectRelation(criteria.subjectRelation())
                .objectType(criteria.objectType())
                .relation(criteria.relation())
                .maxZookie(criteria.maxZookie())
                .build();
        return toViews(tupleRepository.findBySubject(query));
    }

    @Override
    public List<TupleView> findByObject(TupleQueryCriteria criteria) {
        return toViews(findObjectRows(criteria));
    }

    @Override
    public List<RelationTuple> findDirectTuples(String storeId, ObjectRef object, String relation, Long maxZookie) {
        return findObjectEntities(TupleQueryCriteria.forObject(storeId, object.getType(), object.getId(), relation, maxZookie));
    }

    @Override
    public List<RelationTuple> findTupleLinks(String storeId, ObjectRef object, String relation, Long maxZookie) {
        return findObjectEntities(TupleQueryCriteria.forObject(storeId, object.getType(), object.getId(), relation, maxZookie));
    }

    @Override
    public List<RelationTuple> listObjectCandidates(String storeId, String subjectType, String subjectId, String relation, Long maxZookie) {
        return listTupleEntities(TupleQueryCriteria.forPage(storeId, null, null, relation,
                subjectType, subjectId, null, UNBOUNDED_PAGE_SIZE, null, maxZookie));
    }

    @Override
    public List<RelationTuple> listSubjectCandidates(String storeId, String objectType, String objectId,
                                                     String relation, Long maxZookie) {
        return listTupleEntities(TupleQueryCriteria.forPage(storeId, objectType, objectId, relation,
                null, null, null, UNBOUNDED_PAGE_SIZE, null, maxZookie));
    }

    private List<RelationTuple> listTupleEntities(TupleQueryCriteria criteria) {
        return TupleConverter.toEntityList(listTupleRows(criteria));
    }

    private List<RelationTuple> findObjectEntities(TupleQueryCriteria criteria) {
        return TupleConverter.toEntityList(findObjectRows(criteria));
    }

    private List<RelationTuplePO> listTupleRows(TupleQueryCriteria criteria) {
        return tupleRepository.listTuplesWithFilter(
                criteria.storeId(),
                criteria.objectType(),
                criteria.objectId(),
                criteria.relation(),
                criteria.subjectType(),
                criteria.subjectId(),
                criteria.subjectRelation(),
                criteria.effectivePageSize(UNBOUNDED_PAGE_SIZE),
                criteria.pageToken(),
                criteria.maxZookie()
        );
    }

    private List<RelationTuplePO> findObjectRows(TupleQueryCriteria criteria) {
        return tupleRepository.findByObject(
                criteria.storeId(),
                criteria.objectType(),
                criteria.objectId(),
                criteria.relation(),
                criteria.maxZookie());
    }

    private List<TupleView> toViews(List<RelationTuplePO> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        return rows.stream().map(this::toView).toList();
    }

    private TupleView toView(RelationTuplePO row) {
        return new TupleView(
                row.getId(),
                row.getStoreId(),
                row.getObjectType(),
                row.getObjectId(),
                row.getRelation(),
                row.getSubjectType(),
                row.getSubjectId(),
                row.getSubjectRelation(),
                row.getZookie() == null ? null : String.valueOf(row.getZookie()),
                row.getConditionName(),
                row.getConditionContext(),
                row.getExpiresAt());
    }
}
