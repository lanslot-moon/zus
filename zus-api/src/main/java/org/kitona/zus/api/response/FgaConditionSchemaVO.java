package org.kitona.zus.api.response;

import lombok.Data;

import java.util.Map;

/**
 * FGA 条件 Schema VO。
 *
 * <p>条件名称由外层 {@code conditions} Map 的 key 表达。
 */
@Data
public class FgaConditionSchemaVO {

    /**
     * CEL 表达式。
     */
    private String expression;

    /**
     * 参数结构。
     */
    private Map<String, String> parameterSchema;

    /**
     * 条件描述。
     */
    private String description;
}
