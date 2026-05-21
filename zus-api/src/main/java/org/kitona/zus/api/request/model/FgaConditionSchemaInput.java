package org.kitona.zus.api.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 条件 Schema 输入。
 *
 * <p>条件名称由外层 {@code conditions} Map 的 key 提供，value 只描述 CEL 表达式和参数结构。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaConditionSchemaInput {

    /**
     * CEL 表达式。
     */
    @NotBlank(message = "condition.expression 不能为空")
    @Size(max = 1024, message = "condition.expression 长度不能超过 1024")
    private String expression;

    /**
     * 参数结构定义。
     */
    private Map<String, String> parameterSchema;

    /**
     * 条件描述。
     */
    @Size(max = 256, message = "condition.description 长度不能超过 256")
    private String description;
}
