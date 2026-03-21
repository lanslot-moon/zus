package org.kitona.zus.service.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 添加类型定义命令
 *
 * <p>用于向授权模型添加新的类型定义（如 document、folder、user 等）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTypeDefinitionCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    /**
     * 模型ID
     */
    @NotBlank(message = "modelId 不能为空")
    private String modelId;

    /**
     * 资源类型名，如 document、folder、user
     */
    @NotBlank(message = "type 不能为空")
    private String type;

    /**
     * 关系名到重写表达式的映射
     * <p>键为关系名（如 viewer、editor、owner），值为表达式字符串（如 self、self or owner）
     */
    private Map<String, String> relations;

    /**
     * 关系的类型限制（可选）
     * <p>键为关系名，值为允许的主体类型列表，如 parentFolder -> ["folder"]
     */
    private Map<String, List<String>> relationRestrictions;
}
