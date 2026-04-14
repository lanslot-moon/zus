package org.kitona.zus.domain.read.criteria;

/**
 * tuple 存在性查询条件。
 *
 * @param storeId 存储空间
 * @param objectType 对象类型
 * @param objectId 对象标识
 * @param relation 关系
 * @param subjectType 主体类型
 * @param subjectId 主体标识
 * @param subjectRelation 主体关系
 * @param maxZookie 一致性版本
 */
public record TupleExistenceCriteria(
        String storeId,
        String objectType,
        String objectId,
        String relation,
        String subjectType,
        String subjectId,
        String subjectRelation,
        Long maxZookie
) {

    public static TupleExistenceCriteria direct(String storeId, String objectType, String objectId, String relation,
                                                String subjectType, String subjectId, String subjectRelation,
                                                Long maxZookie) {
        return new TupleExistenceCriteria(storeId, objectType, objectId, relation, subjectType, subjectId,
                subjectRelation, maxZookie);
    }

    public static TupleExistenceCriteria wildcard(String storeId, String objectType, String objectId,
                                                  String relation, Long maxZookie) {
        return new TupleExistenceCriteria(storeId, objectType, objectId, relation, null, "*", null, maxZookie);
    }
}
