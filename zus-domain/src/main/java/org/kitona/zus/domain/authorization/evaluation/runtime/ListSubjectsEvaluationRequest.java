package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.Map;

/**
 * ListSubjects 求值请求。
 *
 * <p>该请求描述“哪些主体对指定对象关系具备权限”的搜索命题。store、object、relation 和
 * consistency 构成查询命题的必填部分；context、subjectType 和 subjectRelation 属于条件上下文
 * 或候选裁剪条件，应通过 with 方法按需补充，避免把可选参数伪装成基础命题的一部分。
 */
public final class ListSubjectsEvaluationRequest {

    /**
     * 存储空间标识。
     */
    private final String storeId;

    /**
     * 查询对象。
     */
    private final ObjectRef object;

    /**
     * 查询关系。
     */
    private final String relation;

    /**
     * 一致性令牌。
     */
    private final Zookie zookie;

    /**
     * 可选条件求值上下文。
     */
    private final Map<String, Object> context;

    /**
     * 可选主体类型裁剪条件。
     */
    private final String subjectType;

    /**
     * 可选主体关系裁剪条件。
     */
    private final String subjectRelation;

    private ListSubjectsEvaluationRequest(String storeId, ObjectRef object, String relation, Zookie zookie,
                                          Map<String, Object> context, String subjectType, String subjectRelation) {
        this.storeId = storeId;
        this.object = object;
        this.relation = relation;
        this.zookie = zookie == null ? Zookie.EMPTY : zookie;
        this.context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
        this.subjectType = subjectType;
        this.subjectRelation = subjectRelation;
    }

    /**
     * 创建 ListSubjects 求值请求。
     *
     * <p>该工厂方法只接收构成查询命题必不可少的参数。context、subjectType 和 subjectRelation
     * 都属于条件上下文或候选裁剪条件，应通过 with 方法在必要时补充。
     *
     * @param storeId  存储空间标识
     * @param object   查询对象
     * @param relation 查询关系
     * @param zookie   一致性令牌
     * @return ListSubjects 求值请求
     */
    public static ListSubjectsEvaluationRequest of(String storeId, ObjectRef object, String relation, Zookie zookie) {
        return new ListSubjectsEvaluationRequest(storeId, object, relation, zookie, Collections.emptyMap(), null, null);
    }

    /**
     * 返回携带条件求值上下文的新请求。
     *
     * @param context 条件上下文
     * @return 新请求
     */
    public ListSubjectsEvaluationRequest withContext(Map<String, Object> context) {
        return new ListSubjectsEvaluationRequest(storeId, object, relation, zookie, context, subjectType,
                subjectRelation);
    }

    /**
     * 返回携带主体类型裁剪条件的新请求。
     *
     * @param subjectType 主体类型
     * @return 新请求
     */
    public ListSubjectsEvaluationRequest withSubjectType(String subjectType) {
        return new ListSubjectsEvaluationRequest(storeId, object, relation, zookie, context, subjectType,
                subjectRelation);
    }

    /**
     * 返回携带主体关系裁剪条件的新请求。
     *
     * @param subjectRelation 主体关系
     * @return 新请求
     */
    public ListSubjectsEvaluationRequest withSubjectRelation(String subjectRelation) {
        return new ListSubjectsEvaluationRequest(storeId, object, relation, zookie, context, subjectType,
                subjectRelation);
    }

    /**
     * 返回存储空间标识。
     *
     * @return 存储空间标识
     */
    public String storeId() {
        return storeId;
    }

    /**
     * 返回查询对象。
     *
     * @return 查询对象
     */
    public ObjectRef object() {
        return object;
    }

    /**
     * 返回查询关系。
     *
     * @return 查询关系
     */
    public String relation() {
        return relation;
    }

    /**
     * 返回一致性令牌。
     *
     * @return 一致性令牌
     */
    public Zookie zookie() {
        return zookie;
    }

    /**
     * 返回条件求值上下文。
     *
     * @return 条件求值上下文
     */
    public Map<String, Object> context() {
        return context;
    }

    /**
     * 返回主体类型裁剪条件。
     *
     * @return 主体类型
     */
    public String subjectType() {
        return subjectType;
    }

    /**
     * 返回主体关系裁剪条件。
     *
     * @return 主体关系
     */
    public String subjectRelation() {
        return subjectRelation;
    }
}
