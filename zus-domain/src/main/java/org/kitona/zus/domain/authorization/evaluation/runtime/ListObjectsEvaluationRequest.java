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
 */
public final class ListObjectsEvaluationRequest {

    /**
     * 存储空间标识。
     */
    private final String storeId;

    /**
     * 查询主体类型。
     */
    private final String subjectType;

    /**
     * 查询主体标识。
     */
    private final String subjectId;

    /**
     * 查询关系。
     */
    private final String relation;

    /**
     * 一致性令牌。
     */
    private final Zookie zookie;

    /**
     * 可选 userset 关系，用于表达 group:eng#member 这类主体。
     */
    private final String subjectRelation;

    /**
     * 可选条件求值上下文。
     */
    private final Map<String, Object> context;

    /**
     * 可选对象类型裁剪条件。
     */
    private final String objectType;

    private ListObjectsEvaluationRequest(String storeId, String subjectType, String subjectId, String relation,
                                         Zookie zookie, String subjectRelation, Map<String, Object> context,
                                         String objectType) {
        this.storeId = storeId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.relation = relation;
        this.zookie = zookie == null ? Zookie.EMPTY : zookie;
        this.subjectRelation = subjectRelation;
        this.context = context == null ? Collections.emptyMap() : Collections.unmodifiableMap(context);
        this.objectType = objectType;
    }

    /**
     * 创建 ListObjects 求值请求。
     *
     * <p>该工厂方法只接收构成查询命题必不可少的参数。subjectRelation、objectType、context
     * 都属于可选的主体修饰、候选裁剪或条件上下文，应通过 with 方法在必要时补充。
     *
     * @param storeId     存储空间标识
     * @param subjectType 查询主体类型
     * @param subjectId   查询主体标识
     * @param relation    查询关系
     * @param zookie      一致性令牌
     * @return ListObjects 求值请求
     */
    public static ListObjectsEvaluationRequest of(String storeId, String subjectType, String subjectId, String relation,
                                                  Zookie zookie) {
        return new ListObjectsEvaluationRequest(storeId, subjectType, subjectId, relation, zookie, null,
                Collections.emptyMap(), null);
    }

    /**
     * 返回携带 userset 主体关系的新请求。
     *
     * @param subjectRelation 主体关系
     * @return 新请求
     */
    public ListObjectsEvaluationRequest withSubjectRelation(String subjectRelation) {
        return new ListObjectsEvaluationRequest(storeId, subjectType, subjectId, this.relation, zookie, subjectRelation,
                context, objectType);
    }

    /**
     * 返回携带条件求值上下文的新请求。
     *
     * @param context 条件上下文
     * @return 新请求
     */
    public ListObjectsEvaluationRequest withContext(Map<String, Object> context) {
        return new ListObjectsEvaluationRequest(storeId, subjectType, subjectId, relation, zookie, subjectRelation,
                context, objectType);
    }

    /**
     * 返回携带对象类型裁剪条件的新请求。
     *
     * @param objectType 对象类型
     * @return 新请求
     */
    public ListObjectsEvaluationRequest withObjectType(String objectType) {
        return new ListObjectsEvaluationRequest(storeId, subjectType, subjectId, relation, zookie, subjectRelation,
                context, objectType);
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
     * 返回查询主体类型。
     *
     * @return 查询主体类型
     */
    public String subjectType() {
        return subjectType;
    }

    /**
     * 返回查询主体标识。
     *
     * @return 查询主体标识
     */
    public String subjectId() {
        return subjectId;
    }

    /**
     * 返回可选主体关系。
     *
     * @return 主体关系
     */
    public String subjectRelation() {
        return subjectRelation;
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
     * 返回对象类型裁剪条件。
     *
     * @return 对象类型
     */
    public String objectType() {
        return objectType;
    }

    /**
     * 根据查询参数构造完整主体值对象。
     *
     * @return 查询主体
     */
    public Subject subject() {
        if (isBlank(subjectRelation)) {
            return Subject.user(subjectType, subjectId);
        }
        return Subject.userset(subjectType, subjectId, subjectRelation);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
