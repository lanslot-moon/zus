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
 * <p>写侧只接受结构化 schema-first 输入，DSL 文本仅作为发布后的快照输出，
 * 不再作为创建模型时的并列真相来源。
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
    @NotEmpty(message = "typeDefinitions 不能为空")
    private List<TypeDefinitionInput> typeDefinitions;

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
        @NotBlank(message = "type 不能为空")
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

        @NotBlank(message = "relationName 不能为空")
        private String relationName;

        @NotBlank(message = "rewriteExpression 不能为空")
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

        @NotBlank(message = "name 不能为空")
        private String name;

        @NotBlank(message = "expression 不能为空")
        private String expression;

        private String parameterSchema;

        private String description;
    }
}
