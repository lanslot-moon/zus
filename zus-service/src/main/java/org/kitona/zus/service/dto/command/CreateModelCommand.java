package org.kitona.zus.service.dto.command;

import jakarta.validation.Valid;
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
 * 创建授权模型命令
 *
 * <p>用于创建授权模型的 CQRS 命令对象。
 * <p>支持两种方式定义模型：
 * <ul>
 *   <li>通过 {@link #typeDefinitions} 结构化定义</li>
 *   <li>通过 {@link #dslText} DSL 文本定义</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateModelCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    /**
     * 模型 Schema 版本，如 1.1
     */
    @NotBlank(message = "schemaVersion 不能为空")
    private String schemaVersion;

    /**
     * 类型定义列表（与 dslText 二选一）
     */
    @Valid
    private List<TypeDefinitionInput> typeDefinitions;

    /**
     * 授权模型 DSL 文本（与 typeDefinitions 二选一）
     */
    private String dslText;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 类型定义输入（内部类）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeDefinitionInput implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 资源类型名，如 document、folder、user
         */
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
}
