package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.entity.RelationTupleEntity;

import java.util.List;

/**
 * 关系元组查询仓储接口（读侧）
 *
 * <p>面向分页、过滤、列表类只读查询，不承担聚合装载与写入职责。
 */
public interface ITupleQueryRepository {

    /**
     * 分页列出关系元组（基础方法）
     */
    List<RelationTupleEntity> listTuples(String storeId, String objectType, String relation, int pageSize, Long pageToken);

    /**
     * 分页列出关系元组（支持完整过滤条件）
     */
    List<RelationTupleEntity> listTuplesWithFilter(String storeId, String objectType, String objectId,
                                                   String relation, String subjectType, String subjectId,
                                                   String subjectRelation, int pageSize, Long pageToken);

    /**
     * 根据主体查询元组
     */
    List<RelationTupleEntity> findBySubject(String storeId, String subjectType, String subjectId, String subjectRelation,
                                            String objectType, String relation, Long maxZookie);

    /**
     * 根据资源对象查询元组
     */
    List<RelationTupleEntity> findByObject(String storeId, String objectType, String objectId, String relation, Long maxZookie);
}
