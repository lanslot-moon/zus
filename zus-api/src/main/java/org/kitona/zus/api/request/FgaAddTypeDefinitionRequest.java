package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

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

    /**
     * 关系名到重写表达式的映射。
     * 键为关系名（如 viewer、editor、owner），值为表达式字符串（如 self、self or owner）。
     */
    @NotEmpty(message = "relations 不能为空")
    private Map<String, String> relations;

    /**
     * 关系的类型限制（可选）。
     * 键为关系名，值为允许的主体类型列表，如 parentFolder -> ["folder"]。
     */
    private Map<String, List<String>> relationRestrictions;
}
