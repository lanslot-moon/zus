package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
