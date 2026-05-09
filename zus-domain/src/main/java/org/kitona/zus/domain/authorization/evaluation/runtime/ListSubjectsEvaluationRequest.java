package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.Map;

/**
 * ListSubjects 求值请求。
 *
 * <p>该请求描述“哪些主体对指定对象关系具备权限”的搜索命题。
 * 入口必须携带明确 object 和 relation，搜索过程只从读侧候选集中枚举 subject，
 * 再复用 Check 内核证明每一个候选 subject 是否真正成立。
 *
 * @param storeId  存储空间标识
 * @param object   查询对象
 * @param relation 查询关系
 * @param zookie   一致性令牌
 * @param context  条件求值上下文
 */
public record ListSubjectsEvaluationRequest(String storeId,
                                            ObjectRef object,
                                            String relation,
                                            Zookie zookie,
                                            Map<String, Object> context) {

    /**
     * 归一化上下文和一致性令牌，保证搜索求值过程面对不可变请求数据。
     */
    public ListSubjectsEvaluationRequest {
        zookie = zookie == null ? Zookie.EMPTY : zookie;
        context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
    }

    /**
     * 创建 ListSubjects 求值请求。
     *
     * @param storeId  存储空间标识
     * @param object   查询对象
     * @param relation 查询关系
     * @param zookie   一致性令牌
     * @param context  条件求值上下文
     * @return ListSubjects 求值请求
     */
    public static ListSubjectsEvaluationRequest of(String storeId, ObjectRef object, String relation, Zookie zookie,
                                                   Map<String, Object> context) {
        return new ListSubjectsEvaluationRequest(storeId, object, relation, zookie, context);
    }
}
