package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA 添加关系定义请求
 */
@Data
public class FgaAddRelationRequest {

    /**
     * 关系名称，如 viewer、editor、owner
     */
    @NotBlank(message = "relationName 不能为空")
    @Size(max = 64, message = "relationName 长度不能超过 64")
    private String relationName;

    /**
     * 重写表达式，如 self、self or owner
     */
    @NotBlank(message = "rewriteExpression 不能为空")
    @Size(max = 255, message = "rewriteExpression 长度不能超过 255")
    private String rewriteExpression;

    /**
     * 允许的主体类型列表（可选），如 ["user", "group#member"]
     */
    private List<String> allowedTypes;
}
