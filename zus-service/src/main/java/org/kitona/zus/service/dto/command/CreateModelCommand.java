package org.kitona.zus.service.dto.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
@Getter
@Setter
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
     * 类型定义列表（结构化模型输入）
     */
    @Valid
    private List<TypeDefinitionInput> typeDefinitions;

    /**
     * 授权模型 DSL 快照文本（可选导入/回显字段，不作为写侧真相）
     */
    private String dslText;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 条件定义列表。
     */
    @Valid
    private List<ConditionDefinitionInput> conditions;

    /**
     * 类型定义输入（内部类）
     */
    @Getter
    @Setter
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

        @Valid
        @NotEmpty(message = "relations 不能为空")
        private List<RelationInput> relations;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationInput implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String relationName;

        private String rewriteExpression;

        private List<String> allowedSubjectTypes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionDefinitionInput implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String name;

        private String expression;

        private String parameterSchema;

        private String description;
    }
}
