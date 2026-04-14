package org.kitona.zus.domain.authorization.tuple;

/**
 * 关系元组轻量描述对象。
 *
 * @param subjectType     主体类型
 * @param subjectId       主体标识
 * @param subjectRelation 主体关系，直接主体为 ""
 * @param resourceType    资源类型
 * @param resourceId      资源标识
 * @param relationName    关系名称
 * @param wildcard        是否通配符主体
 * @param expiresAt       过期时间
 * @param condition       条件信息
 */
public record TupleDescriptor(
        String subjectType,
        String subjectId,
        String subjectRelation,
        String resourceType,
        String resourceId,
        String relationName,
        boolean wildcard,
        Long expiresAt,
        TupleCondition condition
) {

    public boolean hasCondition() {
        return condition != null && !condition.isEmpty();
    }
}
