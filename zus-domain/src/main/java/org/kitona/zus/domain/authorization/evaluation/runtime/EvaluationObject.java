package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.ObjectRef;

/**
 * 鉴权对象。
 *
 * @param type 对象类型
 * @param id   对象标识
 */
public record EvaluationObject(String type, String id) {

    /**
     * 从通用对象引用转换为求值期对象。
     */
    public static EvaluationObject from(ObjectRef objectRef) {
        return new EvaluationObject(objectRef.getType(), objectRef.getId());
    }

    /**
     * 还原为领域通用对象引用。
     */
    public ObjectRef toObjectRef() {
        return ObjectRef.of(type, id);
    }
}
