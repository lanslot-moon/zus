package org.kitona.zus.domain.authorization.evaluation.specification;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 主体匹配规则。
 *
 * <p>用于判断某条 tuple 是否直接命中当前要检查的 subject。
 */
public final class SubjectMatchSpecification {

    /**
     * 判定 tuple 的主体部分是否与请求主体相符。
     */
    public static boolean isSatisfiedBy(RelationTuple tuple, Subject subject) {
        if (tuple.hasWildcardSubject()) {
            return !subject.isUserset() && tuple.getSubjectType().equals(subject.getType());
        }

        String expectedRelation = normalize(subject.getRelation());
        return tuple.getSubjectType().equals(subject.getType())
                && tuple.getSubjectId().equals(subject.getId())
                && normalize(tuple.getSubjectRelation()).equals(expectedRelation);
    }

    /**
     * 把空 relation 统一为领域内部的空字符串语义。
     */
    private static String normalize(String relation) {
        return relation == null ? "" : relation;
    }
}
