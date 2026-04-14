package org.kitona.zus.domain.read.criteria;

/**
 * tuple 查询条件。
 *
 * <p>以不可变查询对象替代长参数列表，统一承载 object / subject / relation / 分页 /
 * 一致性读取等读侧查询条件。
 *
 * @param storeId 存储空间
 * @param objectType 对象类型
 * @param objectId 对象标识
 * @param relation 关系名
 * @param subjectType 主体类型
 * @param subjectId 主体标识
 * @param subjectRelation 主体关系
 * @param pageSize 分页大小
 * @param pageToken 分页游标
 * @param maxZookie 最大一致性版本
 */
public record TupleQueryCriteria(
        String storeId,
        String objectType,
        String objectId,
        String relation,
        String subjectType,
        String subjectId,
        String subjectRelation,
        Integer pageSize,
        Long pageToken,
        Long maxZookie
) {

    public static TupleQueryCriteria forPage(String storeId, String objectType, String objectId, String relation,
                                             String subjectType, String subjectId, String subjectRelation,
                                             Integer pageSize, Long pageToken) {
        return forPage(storeId, objectType, objectId, relation, subjectType, subjectId,
                subjectRelation, pageSize, pageToken, null);
    }

    public static TupleQueryCriteria forPage(String storeId, String objectType, String objectId, String relation,
                                             String subjectType, String subjectId, String subjectRelation,
                                             Integer pageSize, Long pageToken, Long maxZookie) {
        return new TupleQueryCriteria(storeId, objectType, objectId, relation, subjectType, subjectId,
                subjectRelation, pageSize, pageToken, maxZookie);
    }

    public static TupleQueryCriteria forSubject(String storeId, String subjectType, String subjectId,
                                                String subjectRelation, String objectType, String relation,
                                                Long maxZookie) {
        return new TupleQueryCriteria(storeId, objectType, null, relation, subjectType, subjectId,
                subjectRelation, null, null, maxZookie);
    }

    public static TupleQueryCriteria forObject(String storeId, String objectType, String objectId,
                                               String relation, Long maxZookie) {
        return new TupleQueryCriteria(storeId, objectType, objectId, relation, null, null,
                null, null, null, maxZookie);
    }

    public int effectivePageSize(int fallback) {
        return pageSize != null ? pageSize : fallback;
    }
}
