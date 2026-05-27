package org.kitona.zus.service.dto.command;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 写入元组命令
 *
 * <p>用于写入关系元组的 CQRS 命令对象。
 * <p>元组格式：{@code object#relation@subject}
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
public class WriteTupleCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 7031456908792938039L;

    /**
     * 资源类型，如 document、folder
     */
    @NotBlank(message = "objectType 不能为空")
    private String objectType;

    /**
     * 资源ID
     */
    @NotBlank(message = "objectId 不能为空")
    private String objectId;

    /**
     * 关系名称，如 viewer、editor、owner
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 主体类型，如 user、group
     */
    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    /**
     * 主体关系（用户集时使用，可选）
     * <p>如：group:engineering#member 中的 member
     */
    private String subjectRelation;

    /**
     * 条件名称（可选）
     */
    private String conditionName;

    /**
     * 条件定义ID（可选）。
     *
     * <p>外部请求通常只需要提供 conditionName，应用层会在转换为领域请求时
     * 根据授权模型解析 conditionDefinitionId。该字段保留给内部调用或后续扩展，
     * 但不会再作为请求必须携带的条件引用。
     */
    @Positive(message = "conditionDefinitionId 必须大于 0")
    private Long conditionDefinitionId;

    /**
     * 条件上下文（可选，JSON 字符串）
     */
    private String conditionContext;

    /**
     * 过期时间（毫秒）。
     */
    private Long expiresAt;

    /**
     * 审计信息。
     */
    private AuditMetadataInput auditMetadata;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditMetadataInput implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 操作人标识。
         */
        private String operatorId;

        /**
         * 请求幂等或链路追踪标识。
         */
        private String requestId;

        /**
         * 操作来源，如控制台、开放 API 或内部任务。
         */
        private String source;
    }

    /**
     * 判断条件上下文是否具备可解析的条件引用。
     *
     * @return 满足条件返回 true，否则返回 false
     */
    @AssertTrue(message = "存在条件上下文时必须提供 conditionName 或 conditionDefinitionId")
    public boolean isConditionReferenceValid() {
        boolean hasConditionContext = conditionContext != null && !conditionContext.isBlank();
        boolean hasConditionReference = (conditionName != null && !conditionName.isBlank())
                || conditionDefinitionId != null;
        return !hasConditionContext || hasConditionReference;
    }
}
