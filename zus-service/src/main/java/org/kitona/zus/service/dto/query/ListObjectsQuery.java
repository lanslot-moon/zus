package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列出对象查询
 *
 * <p>用于查询指定主体可访问的对象列表。
 * <p>查询语义：列出 subject 通过 relation 可访问的所有 object。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

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
     * 主体关系（用于 userset，可选）
     */
    private String subjectRelation;

    /**
     * 关系名称，如 viewer、editor
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 资源类型过滤（可选）
     */
    private String objectType;
}
