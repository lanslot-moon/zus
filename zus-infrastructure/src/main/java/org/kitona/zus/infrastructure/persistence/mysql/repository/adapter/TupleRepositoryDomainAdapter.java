package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.TupleConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleKeyQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleSubjectQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.WildcardTupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.repository.impl.TuplePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 关系元组仓储领域接口适配器
 * 
 * 实现 domain 层 ITupleRepository，委托基础设施层仓储并做 PO↔领域实体 转换。
 * 按照 DDD 规范，Repository 操作领域实体 {@link RelationTupleEntity}。
 *
 * @author kitona
 */
@Repository
public class TupleRepositoryDomainAdapter implements ITupleDomainRepository {

    @Resource
    private TuplePersistenceRepository tupleRepository;

    @Override
    public boolean existsTuple(String storeId, String objectType, String objectId, String relation,
                               String subjectType, String subjectId, String subjectRelation, Long maxZookie) {
        TupleExistsQuery query = TupleExistsQuery.builder()
                .storeId(storeId)
                .objectType(objectType)
                .objectId(objectId)
                .relation(relation)
                .subjectType(subjectType)
                .subjectId(subjectId)
                .subjectRelation(subjectRelation)
                .maxZookie(maxZookie)
                .build();
        return tupleRepository.existsTuple(query);
    }

    @Override
    public boolean existsWildcardTuple(String storeId, String objectType, String objectId, String relation,
                                       Long maxZookie) {
        WildcardTupleExistsQuery query = WildcardTupleExistsQuery.builder()
                .storeId(storeId)
                .objectType(objectType)
                .objectId(objectId)
                .relation(relation)
                .maxZookie(maxZookie)
                .build();
        return tupleRepository.existsWildcardTuple(query);
    }

    @Override
    public List<RelationTupleEntity> findByObjectAndRelation(String storeId, String objectType, String objectId,
                                                        String relation, Long maxZookie) {
        List<TuplePO> list = tupleRepository.findByObjectAndRelation(storeId, objectType, objectId, relation, maxZookie);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
//        return list.stream()
//                .map(po -> new RelationTuple(po.getSubjectType(), po.getSubjectId(), po.getObjectType(), po.getObjectId(), po.getRelation()))
//                .collect(Collectors.toList());
        return TupleConverter.toEntityList(list);
    }

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

    @Override
    public boolean saveBatch(List<RelationTupleEntity> tuples) {
        if (tuples == null || tuples.isEmpty()) {
            return true;
        }
        return tupleRepository.batchCreate(TupleConverter.toPOList(tuples));
    }

    @Override
    public Optional<RelationTupleEntity> findByTupleKey(String storeId, String objectType, String objectId, String relation,
                                             String subjectType, String subjectId, String subjectRelation) {
        TupleKeyQuery query = new TupleKeyQuery();
        query.setStoreId(storeId);
        query.setObjectType(objectType);
        query.setObjectId(objectId);
        query.setRelation(relation);
        query.setSubjectType(subjectType);
        query.setSubjectId(subjectId);
        query.setSubjectRelation(subjectRelation);
        return tupleRepository.findByTupleKey(query).map(TupleConverter::toEntity);
    }

    @Override
    public boolean batchDelete(String storeId, List<Long> ids) {
        return ids == null || ids.isEmpty() || tupleRepository.batchDelete(storeId, ids);
    }

    @Override
    public List<RelationTupleEntity> findByTupleKeys(String storeId, List<TupleKey> tupleKeys) {
        if (tupleKeys == null || tupleKeys.isEmpty()) {
            return Collections.emptyList();
        }

        // 将领域层 TupleKey 转换为基础设施层 TupleKeyQuery
        List<TupleKeyQuery> queries = tupleKeys.stream()
                .map(key -> {
                    TupleKeyQuery query = new TupleKeyQuery();
                    query.setStoreId(storeId);
                    query.setObjectType(key.getObjectType());
                    query.setObjectId(key.getObjectId());
                    query.setRelation(key.getRelation());
                    query.setSubjectType(key.getSubjectType());
                    query.setSubjectId(key.getSubjectId());
                    query.setSubjectRelation(key.getSubjectRelation());
                    return query;
                })
                .collect(Collectors.toList());

        List<TuplePO> poList = tupleRepository.findByTupleKeys(storeId, queries);
        return TupleConverter.toEntityList(poList);
    }
}
