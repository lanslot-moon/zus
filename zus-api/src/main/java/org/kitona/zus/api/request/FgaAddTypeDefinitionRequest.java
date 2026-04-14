package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA 添加类型定义请求
 */
@Data
public class FgaAddTypeDefinitionRequest {

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
