package org.kitona.zus.service.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限检查命令
 *
 * <p>用于 Check API 的请求参数传输。
 * <p>检查 subject 是否对 object 拥有指定的 relation。
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
public class CheckCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 5068573299682003948L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

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
     */
    private String subjectRelation;

    /**
     * Zookie 令牌（用于一致性读取，可选）
     * <p>传入后将只查询该版本之前的元组，保证一致性读取
     */
    private String consistencyToken;

    /**
     * 请求上下文（供 condition 使用）。
     */
    private java.util.Map<String, Object> context;
}
