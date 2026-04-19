package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 条件定义 VO —— 对应 {@code fga_condition_definition}
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaConditionVO {

    /**
     * 条件名
     */
    private String name;

    /**
     * CEL 表达式
     */
    private String expression;

    /**
     * 参数结构（字段名 → 类型描述）
     */
    private Map<String, String> parameterSchema;

    /**
     * 条件描述
     */
    private String description;
}
