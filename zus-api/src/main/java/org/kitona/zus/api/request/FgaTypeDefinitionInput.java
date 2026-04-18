package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA 类型定义输入对象
 *
 * <p>用于创建授权模型时的类型定义入参。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
@Data
public class FgaTypeDefinitionInput {

    /**
     * 资源类型名，如 document、folder、user
     */
    @NotBlank(message = "type 不能为空")
    @Size(max = 64, message = "type 长度不能超过 64")
    private String type;

    @Valid
    @NotEmpty(message = "relations 不能为空")
    private List<FgaRelationDefinitionInput> relations;
}
