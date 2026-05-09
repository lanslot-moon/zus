package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列出主体查询。
 *
 * <p>用于查询对指定 object 的 relation 具备权限的 subject 列表。
 * subject 可以是直接主体、userset 或 wildcard，不局限于自然人用户。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListSubjectsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 关系名称，如 viewer、editor
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 主体类型过滤（可选）。
     */
    private String subjectType;

    /**
     * 主体关系过滤（可选）。
     */
    private String subjectRelation;

    /**
     * 一致性令牌。
     */
    private String consistencyToken;

    /**
     * 请求上下文。
     */
    private java.util.Map<String, Object> context;
}
