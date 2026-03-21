package org.kitona.zus.service.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
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
}
