package org.kitona.zus.api.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 关系重写表达式在 API 侧的粗粒度分类。
 *
 * <p>该分类仅用于响应展示，帮助调用方快速识别 relation 的主要语义类型，
 * 不参与领域判定，也不替代领域层的正式 rewrite AST。
 *
 * @author kitona
 * @since 2026-04-19
 */
public enum FgaRelationType {

    DIRECT_ONLY(0),
    COMPUTED_USERSET(1),
    TUPLE_TO_USERSET(2),
    COMPOSITE(3);

    private static final String TUPLE_TO_USERSET_OPERATOR = " from ";
    private static final String UNION_OPERATOR = " or ";
    private static final String INTERSECTION_OPERATOR = " and ";
    private static final String EXCLUSION_OPERATOR = " but not ";
    private static final String SELF_KEYWORD = "self";
    private static final String THIS_KEYWORD = "this";

    private final int code;

    FgaRelationType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据 rewrite 表达式推导 API 展示类型。
     */
    public static Integer deriveCode(String rewriteExpression) {
        if (StringUtils.isBlank(rewriteExpression)) {
            return null;
        }

        String normalizedExpression = rewriteExpression.trim();
        if (normalizedExpression.contains(TUPLE_TO_USERSET_OPERATOR)) {
            return TUPLE_TO_USERSET.code;
        }
        if (normalizedExpression.contains(UNION_OPERATOR)
                || normalizedExpression.contains(INTERSECTION_OPERATOR)
                || normalizedExpression.contains(EXCLUSION_OPERATOR)) {
            return COMPOSITE.code;
        }
        if (SELF_KEYWORD.equals(normalizedExpression) || THIS_KEYWORD.equals(normalizedExpression)) {
            return DIRECT_ONLY.code;
        }
        return COMPUTED_USERSET.code;
    }
}
