package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.Subject;

/**
 * 鉴权主体。
 *
 * @param type     主体类型
 * @param id       主体标识
 * @param relation 主体关系
 */
public record EvaluationSubject(String type, String id, String relation) {

    /**
     * 从通用主体对象转换为求值期主体。
     */
    public static EvaluationSubject from(Subject subject) {
        return new EvaluationSubject(subject.getType(), subject.getId(), subject.getRelation());
    }

    /**
     * 还原为领域通用主体对象。
     *
     * <p>这里会根据 relation 和 wildcard 标记恢复 direct subject、userset subject 或 wildcard subject。
     */
    public Subject toSubject() {
        if (Subject.WILDCARD.equals(id)) {
            return Subject.wildcard(type);
        }
        if (relation == null || relation.isEmpty()) {
            return Subject.user(type, id);
        }
        return Subject.userset(type, id, relation);
    }

    /**
     * 判断当前主体是否为 userset 形式。
     */
    public boolean isUserset() {
        return relation != null && !relation.isEmpty();
    }
}
