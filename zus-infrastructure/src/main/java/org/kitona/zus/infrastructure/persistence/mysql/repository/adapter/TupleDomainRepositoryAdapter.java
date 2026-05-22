package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.infrastructure.persistence.mysql.converter.TupleConverter;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleKeyQuery;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ITuplePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 关系元组仓储领域接口适配器
 *
 * <p>实现 domain 层 ITupleDomainRepository，委托基础设施层仓储并做 PO↔领域实体转换。
 */
@Repository
public class TupleDomainRepositoryAdapter implements ITupleDomainRepository {

    @Resource
    private ITuplePersistenceRepository tupleRepository;

    /**
     * 按 tuple key 查询关系元组。
     *
     * @param storeId Store 标识
     * @param tupleKey tuple 键
     * @return 查询结果
     */
    @Override
    public Optional<RelationTuple> findByKey(String storeId, TupleKey tupleKey) {
        if (tupleKey == null) {
            return Optional.empty();
        }
        return tupleRepository.findByTupleKey(toKeyQuery(storeId, tupleKey)).map(TupleConverter::toEntity);
    }

    /**
     * 保存save。
     *
     * @param tuple 关系元组
     */
    @Override
    public void save(RelationTuple tuple) {
        if (tuple == null) {
            return;
        }
        tupleRepository.batchCreate(TupleConverter.toPOList(List.of(tuple)));
    }

    /**
     * 移除remove。
     *
     * @param tuple 关系元组
     */
    @Override
    public void remove(RelationTuple tuple) {
        if (tuple == null || tuple.getId() == null) {
            return;
        }
        tupleRepository.batchDelete(tuple.getStoreId(), List.of(tuple.getId()));
    }

    /**
     * 转换为 tuple key 查询对象。
     *
     * @param storeId Store 标识
     * @param key key 参数
     * @return 构建结果
     */
    private TupleKeyQuery toKeyQuery(String storeId, TupleKey key) {
        TupleKeyQuery query = new TupleKeyQuery();
        query.setStoreId(storeId);
        query.setObjectType(key.getObjectType());
        query.setObjectId(key.getObjectId());
        query.setRelation(key.getRelation());
        query.setSubjectType(key.getSubjectType());
        query.setSubjectId(key.getSubjectId());
        query.setSubjectRelation(key.getSubjectRelation());
        return query;
    }
}
