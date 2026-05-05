package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

/**
 * explain 中展示的 tuple 详情。
 *
 * @param object              对象
 * @param relation            关系
 * @param subject             主体
 * @param wildcard            是否 wildcard tuple
 * @param zookie              tuple 版本
 * @param expiresAt           过期时间
 * @param conditionDefinitionId 条件定义 ID
 * @param conditionName       条件名称
 */
public record TupleExplainDetail(String object,
                                 String relation,
                                 String subject,
                                 boolean wildcard,
                                 String zookie,
                                 Long expiresAt,
                                 Long conditionDefinitionId,
                                 String conditionName) {

    /**
     * 从领域 tuple 构造解释详情。
     */
    public static TupleExplainDetail from(RelationTuple tuple) {
        return new TupleExplainDetail(
                tuple.getObject().toString(),
                tuple.getRelation(),
                tuple.getSubject().toString(),
                tuple.hasWildcardSubject(),
                tuple.getZookie().toToken(),
                tuple.getExpiresAt(),
                tuple.getConditionDefinitionId(),
                tuple.getConditionName()
        );
    }
}
