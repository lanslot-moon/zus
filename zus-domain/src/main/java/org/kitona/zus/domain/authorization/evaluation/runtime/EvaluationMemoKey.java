package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 单次授权请求内的求值缓存键。
 *
 * <p>同一个主体、对象、关系、zookie 和上下文哈希在一次递归过程中
 * 只应求值一次，以避免重复遍历和循环递归。
 */
public record EvaluationMemoKey(Subject subject, ObjectRef object, String relation, String zookieToken,
                                int contextHash) {

    /**
     * 基于当前请求和递归目标构造 memo key。
     *
     * <p>上下文哈希被纳入 key，是为了区分同一个 relation 在不同 request context 下的条件求值结果。
     */
    public static EvaluationMemoKey of(EvaluationRequest request, Subject subject, ObjectRef object, String relation) {
        return new EvaluationMemoKey(subject, object, relation, request.zookie().toToken(), request.context().hashCode());
    }
}
