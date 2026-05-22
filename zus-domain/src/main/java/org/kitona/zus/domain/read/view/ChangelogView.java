package org.kitona.zus.domain.read.view;

/**
 * 变更日志读侧视图。
 *
 * <p>该类型用于 Watch 和增量同步查询结果，不参与 Changelog 领域实体的写侧行为。
 *
 * @param zookie          变更版本
 * @param operation       操作类型
 * @param objectType      对象类型
 * @param objectId        对象标识
 * @param relation        关系名称
 * @param subjectType     主体类型
 * @param subjectId       主体标识
 * @param subjectRelation 主体关系
 * @param operatorId      操作人
 * @param requestId       请求标识
 * @param source          操作来源
 * @param operationTime   操作时间
 */
public record ChangelogView(Long zookie,
                            String operation,
                            String objectType,
                            String objectId,
                            String relation,
                            String subjectType,
                            String subjectId,
                            String subjectRelation,
                            String operatorId,
                            String requestId,
                            String source,
                            Long operationTime) {
}
