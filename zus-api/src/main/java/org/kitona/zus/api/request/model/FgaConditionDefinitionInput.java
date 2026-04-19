package org.kitona.zus.api.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 条件定义输入 —— 对应数据库表 {@code fga_condition_definition}
 *
 * <p>ABAC 混合模式：用 CEL 表达式描述运行时约束，元组上挂 condition 后，
 * Check 引擎求值通过才视作关系成立。
 *
 * <h3>示例</h3>
 * <pre>
 * name = "is_working_hours"
 * expression = "request.hour &gt;= params.start_hour &amp;&amp; request.hour &lt; params.end_hour"
 * parameterSchema = {"start_hour": "int", "end_hour": "int"}
 * </pre>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaConditionDefinitionInput {

    /**
     * 条件名（模型内唯一）
     */
    @NotBlank(message = "condition.name 不能为空")
    @Size(max = 64, message = "condition.name 长度不能超过 64")
    private String name;

    /**
     * CEL 表达式
     */
    @NotBlank(message = "condition.expression 不能为空")
    @Size(max = 1024, message = "condition.expression 长度不能超过 1024")
    private String expression;

    /**
     * 参数结构定义（key: 参数名，value: 参数类型，如 int / string / bool / list / map）
     */
    private Map<String, String> parameterSchema;

    /**
     * 条件描述
     */
    @Size(max = 256, message = "condition.description 长度不能超过 256")
    private String description;
}
