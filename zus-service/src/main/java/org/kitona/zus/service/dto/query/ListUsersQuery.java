package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列出用户查询
 *
 * <p>用于查询对指定资源有权限的用户列表。
 * <p>查询语义：列出对 object 拥有 relation 权限的所有 subject。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListUsersQuery implements Serializable {

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
}
