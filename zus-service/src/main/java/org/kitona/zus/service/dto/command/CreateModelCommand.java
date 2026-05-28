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
import java.util.Map;

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
     * 类型定义映射，key 为模型内唯一的类型名。
     */
    @Valid
    @NotEmpty(message = "types 不能为空")
    private Map<String, TypeDefinitionInput> types;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 条件定义映射，key 为模型内唯一的条件名。
     */
    @Valid
    private Map<String, ConditionDefinitionInput> conditions;

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
         * 当前类型声明的关系定义映射。
         *
         * <p>relations 允许为空，用于表达 {@code user}、{@code device}、{@code service_account}
         * 这类只作为 subject 参与授权、但自身不承载 object relation 的叶子类型。
         */
        @Valid
        private Map<String, RelationInput> relations;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationInput implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

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

        @NotBlank(message = "expression 不能为空")
        private String expression;

        private String parameterSchema;

        private String description;
    }
}
