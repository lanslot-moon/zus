package org.kitona.zus.domain.authorization.evaluation.specification;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 关系类型限制规则。
 *
 * <p>用于校验 tuple 的 subject 是否落在关系定义允许的 subject type / userset / wildcard 范围内。
 */
public final class RelationRestrictionSpecification {

    /**
     * 判断 tuple 是否满足已编译关系声明的 allowed subject types。
     */
    public static boolean isSatisfiedBy(CompiledRelation relation, RelationTuple tuple) {
        if (relation.restrictions().isEmpty()) {
            return true;
        }
        Subject subject = tuple.getSubject();
        return relation.restrictions().stream().anyMatch(restriction -> matches(restriction, subject));
    }

    /**
     * 匹配单条限制表达式。
     *
     * <p>支持三种格式：{@code user}、{@code group#member}、{@code user:*}。
     */
    private static boolean matches(String restriction, Subject subject) {
        if (restriction == null || restriction.isBlank()) {
            return false;
        }
        if (restriction.endsWith(":*")) {
            String type = restriction.substring(0, restriction.length() - 2);
            return subject.isWildcard() && type.equals(subject.getType());
        }
        int relationIndex = restriction.indexOf('#');
        if (relationIndex > 0) {
            String type = restriction.substring(0, relationIndex);
            String relation = restriction.substring(relationIndex + 1);
            return type.equals(subject.getType()) && relation.equals(subject.getRelation());
        }
        return restriction.equals(subject.getType());
    }
}
