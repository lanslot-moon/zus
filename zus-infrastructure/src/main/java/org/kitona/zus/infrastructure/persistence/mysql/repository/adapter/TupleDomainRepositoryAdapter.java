package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.read.criteria.TupleExistenceCriteria;
import org.kitona.zus.domain.read.criteria.TupleKeyCriteria;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.TupleConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleKeyQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.WildcardTupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ITuplePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 关系元组仓储领域接口适配器
 *
 * <p>实现 domain 层 ITupleDomainRepository，委托基础设施层仓储并做 PO↔领域实体转换。
 */
@Repository
public class TupleDomainRepositoryAdapter implements ITupleDomainRepository {

    @Resource
    private ITuplePersistenceRepository tupleRepository;

    @Override
    public boolean existsTuple(TupleExistenceCriteria criteria) {
        TupleExistsQuery query = TupleExistsQuery.builder()
                .storeId(criteria.storeId())
                .objectType(criteria.objectType())
                .objectId(criteria.objectId())
                .relation(criteria.relation())
                .subjectType(criteria.subjectType())
                .subjectId(criteria.subjectId())
                .subjectRelation(criteria.subjectRelation())
                .maxZookie(criteria.maxZookie())
                .build();
        return tupleRepository.existsTuple(query);
    }

    @Override
    public boolean existsWildcardTuple(TupleExistenceCriteria criteria) {
        WildcardTupleExistsQuery query = WildcardTupleExistsQuery.builder()
                .storeId(criteria.storeId())
                .objectType(criteria.objectType())
                .objectId(criteria.objectId())
                .relation(criteria.relation())
                .maxZookie(criteria.maxZookie())
                .build();
        return tupleRepository.existsWildcardTuple(query);
    }

    @Override
    public List<RelationTuple> findByObjectAndRelation(TupleQueryCriteria criteria) {
        List<TuplePO> list = tupleRepository.findByObjectAndRelation(
                criteria.storeId(), criteria.objectType(), criteria.objectId(), criteria.relation(), criteria.maxZookie());
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return TupleConverter.toEntityList(list);
    }

    @Override
    public boolean saveBatch(List<RelationTuple> tuples) {
        if (tuples == null || tuples.isEmpty()) {
            return true;
        }
        return tupleRepository.batchCreate(TupleConverter.toPOList(tuples));
    }

    @Override
    public Optional<RelationTuple> findByTupleKey(TupleExistenceCriteria criteria) {
        TupleKeyQuery query = new TupleKeyQuery();
        query.setStoreId(criteria.storeId());
        query.setObjectType(criteria.objectType());
        query.setObjectId(criteria.objectId());
        query.setRelation(criteria.relation());
        query.setSubjectType(criteria.subjectType());
        query.setSubjectId(criteria.subjectId());
        query.setSubjectRelation(criteria.subjectRelation());
        return tupleRepository.findByTupleKey(query).map(TupleConverter::toEntity);
    }

    @Override
    public boolean batchDelete(String storeId, List<Long> ids) {
        return ids == null || ids.isEmpty() || tupleRepository.batchDelete(storeId, ids);
    }

    @Override
    public List<RelationTuple> findByTupleKeys(TupleKeyCriteria criteria) {
        if (criteria == null || criteria.tupleKeys() == null || criteria.tupleKeys().isEmpty()) {
            return Collections.emptyList();
        }

        List<TupleKeyQuery> queries = criteria.tupleKeys().stream()
                .map(key -> {
                    TupleKeyQuery query = new TupleKeyQuery();
                    query.setStoreId(criteria.storeId());
                    query.setObjectType(key.getObjectType());
                    query.setObjectId(key.getObjectId());
                    query.setRelation(key.getRelation());
                    query.setSubjectType(key.getSubjectType());
                    query.setSubjectId(key.getSubjectId());
                    query.setSubjectRelation(key.getSubjectRelation());
                    return query;
                })
                .collect(Collectors.toList());

        List<TuplePO> poList = tupleRepository.findByTupleKeys(criteria.storeId(), queries);
        return TupleConverter.toEntityList(poList);
    }
}
