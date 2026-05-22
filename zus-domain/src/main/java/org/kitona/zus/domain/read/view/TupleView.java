package org.kitona.zus.domain.read.view;

/**
 * 关系元组读侧视图。
 *
 * <p>该类型用于 read/list 查询结果，不参与 RelationTuple 聚合根的业务行为，
 * 避免读侧查询接口直接泄漏聚合对象。
 *
 * @param id               元组主键
 * @param storeId          存储空间标识
 * @param objectType       对象类型
 * @param objectId         对象标识
 * @param relation         关系名称
 * @param subjectType      主体类型
 * @param subjectId        主体标识
 * @param subjectRelation  主体关系
 * @param zookie           写入时的 zookie 版本
 * @param conditionName    条件名称快照
 * @param conditionContext 条件上下文快照
 * @param expiresAt        过期时间
 */
public record TupleView(Long id,
                        String storeId,
                        String objectType,
                        String objectId,
                        String relation,
                        String subjectType,
                        String subjectId,
                        String subjectRelation,
                        String zookie,
                        String conditionName,
                        String conditionContext,
                        Long expiresAt) {
}
