package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;

import java.util.List;

/**
 * 关系元组查询仓储接口（读侧）
 *
 * <p>面向分页、过滤、列表类只读查询，不承担聚合装载与写入职责。
 */
public interface ITupleQueryRepository {

    /**
     * 分页列出关系元组。
     */
    List<RelationTuple> list(TupleQueryCriteria criteria);

    /**
     * 根据主体查询元组
     */
    List<RelationTuple> findBySubject(TupleQueryCriteria criteria);

    /**
     * 根据资源对象查询元组
     */
    List<RelationTuple> findByObject(TupleQueryCriteria criteria);
}
