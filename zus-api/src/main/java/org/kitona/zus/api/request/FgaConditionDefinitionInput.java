package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA 条件定义输入对象。
 */
@Data
public class FgaConditionDefinitionInput {

    @NotBlank(message = "name 不能为空")
    @Size(max = 64, message = "name 长度不能超过 64")
    private String name;

    @NotBlank(message = "expression 不能为空")
    @Size(max = 1024, message = "expression 长度不能超过 1024")
    private String expression;

    @Size(max = 4096, message = "parameterSchema 长度不能超过 4096")
    private String parameterSchema;

    @Size(max = 256, message = "description 长度不能超过 256")
    private String description;
}
