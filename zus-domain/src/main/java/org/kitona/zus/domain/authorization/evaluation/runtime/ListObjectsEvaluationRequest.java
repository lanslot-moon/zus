package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.Map;

/**
 * ListObjects 求值请求。
 *
 * <p>该请求描述“某个主体通过指定关系可以访问哪些对象”的搜索命题。
 * 与单点 Check 不同，ListObjects 在入口阶段没有明确 object，因此 objectType 只作为候选裁剪条件，
 * 真正的 object 会在搜索过程中由候选读端口提供，再组装成完整的 {@link EvaluationRequest}
 * 交给 Check 内核验证。
 *
 * @param storeId    存储空间标识
 * @param subject    查询主体
 * @param relation   查询关系
 * @param zookie     一致性令牌
 * @param context    条件求值上下文
 * @param objectType 可选对象类型过滤条件
 */
public record ListObjectsEvaluationRequest(String storeId,
                                           Subject subject,
                                           String relation,
                                           Zookie zookie,
                                           Map<String, Object> context,
                                           String objectType) {

    /**
     * 归一化上下文和一致性令牌，保证搜索求值过程面对不可变请求数据。
     */
    public ListObjectsEvaluationRequest {
        zookie = zookie == null ? Zookie.EMPTY : zookie;
        context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
    }

    /**
     * 创建 ListObjects 求值请求。
     *
     * @param storeId    存储空间标识
     * @param subject    查询主体
     * @param relation   查询关系
     * @param zookie     一致性令牌
     * @param context    条件求值上下文
     * @param objectType 可选对象类型过滤条件
     * @return ListObjects 求值请求
     */
    public static ListObjectsEvaluationRequest of(String storeId, Subject subject, String relation, Zookie zookie,
                                                  Map<String, Object> context, String objectType) {
        return new ListObjectsEvaluationRequest(storeId, subject, relation, zookie, context, objectType);
    }
}
