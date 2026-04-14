package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.Map;

/**
 * 鉴权请求。
 *
 * @param storeId  存储空间
 * @param subject  主体
 * @param object   对象
 * @param relation 关系
 * @param zookie   一致性令牌
 * @param context  请求上下文
 */
public record EvaluationRequest(String storeId, EvaluationSubject subject, EvaluationObject object, String relation,
                                Zookie zookie, Map<String, Object> context) {

    /**
     * 归一化请求上下文和一致性令牌，保证求值器内部始终面对不可变数据。
     */
    public EvaluationRequest {
        context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
        zookie = zookie != null ? zookie : Zookie.EMPTY;
    }

    /**
     * 从通用领域对象构造求值请求。
     */
    public static EvaluationRequest of(String storeId, Subject subject, ObjectRef object,
                                       String relation, Zookie zookie, Map<String, Object> context) {
        return new EvaluationRequest(storeId, EvaluationSubject.from(subject), EvaluationObject.from(object),
                relation, zookie, context);
    }

    /**
     * 派生一个仅替换目标对象的新请求。
     */
    public EvaluationRequest withObject(ObjectRef objectRef) {
        return new EvaluationRequest(storeId, subject, EvaluationObject.from(objectRef), relation, zookie, context);
    }

    /**
     * 派生一个仅替换主体的新请求。
     */
    public EvaluationRequest withSubject(Subject nextSubject) {
        return new EvaluationRequest(storeId, EvaluationSubject.from(nextSubject), object, relation, zookie, context);
    }
}
