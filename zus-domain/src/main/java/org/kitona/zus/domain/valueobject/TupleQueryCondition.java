package org.kitona.zus.domain.valueobject;

import lombok.Builder;
import lombok.Getter;

/**
 * 元组查询条件值对象
 *
 * <p>封装元组查询的过滤条件，将过滤逻辑下沉到 Repository 层。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Getter
@Builder
public class TupleQueryCondition {

    private final String storeId;
    private final String objectType;
    private final String objectId;
    private final String relation;
    private final String subjectType;
    private final String subjectId;
    private final String subjectRelation;
    private final Integer pageSize;
    private final Long pageToken;

    public static TupleQueryCondition of(String storeId) {
        return TupleQueryCondition.builder()
                .storeId(storeId)
                .build();
    }

    public TupleQueryCondition withObjectType(String objectType) {
        return TupleQueryCondition.builder()
                .storeId(this.storeId)
                .objectType(objectType)
                .objectId(this.objectId)
                .relation(this.relation)
                .subjectType(this.subjectType)
                .subjectId(this.subjectId)
                .subjectRelation(this.subjectRelation)
                .pageSize(this.pageSize)
                .pageToken(this.pageToken)
                .build();
    }

    public TupleQueryCondition withPagination(Integer pageSize, Long pageToken) {
        return TupleQueryCondition.builder()
                .storeId(this.storeId)
                .objectType(this.objectType)
                .objectId(this.objectId)
                .relation(this.relation)
                .subjectType(this.subjectType)
                .subjectId(this.subjectId)
                .subjectRelation(this.subjectRelation)
                .pageSize(pageSize)
                .pageToken(pageToken)
                .build();
    }

    public boolean hasObjectFilter() {
        return objectType != null || objectId != null;
    }

    public boolean hasSubjectFilter() {
        return subjectType != null || subjectId != null || subjectRelation != null;
    }

    public boolean hasRelationFilter() {
        return relation != null;
    }
}
