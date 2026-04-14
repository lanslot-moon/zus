package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA 关系定义输入对象。
 */
@Data
public class FgaRelationDefinitionInput {

    @NotBlank(message = "relationName 不能为空")
    @Size(max = 64, message = "relationName 长度不能超过 64")
    private String relationName;

    @NotBlank(message = "rewriteExpression 不能为空")
    @Size(max = 512, message = "rewriteExpression 长度不能超过 512")
    private String rewriteExpression;

    private List<String> allowedSubjectTypes;
}
