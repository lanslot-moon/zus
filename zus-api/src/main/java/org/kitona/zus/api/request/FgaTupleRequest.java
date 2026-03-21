package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA 元组 API 请求项（用于 Write 的 writes/deletes 单项）
 */
@Data
public class FgaTupleRequest {

    /**
     * 资源类型
     */
    @NotBlank(message = "objectType 不能为空")
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;

    /**
     * 资源ID
     */
    @NotBlank(message = "objectId 不能为空")
    @Size(max = 255, message = "objectId 长度不能超过 255")
    private String objectId;

    /**
     * 关系名称
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 主体类型
     */
    @NotBlank(message = "subjectType 不能为空")
    @Size(max = 64, message = "subjectType 长度不能超过 64")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    @Size(max = 255, message = "subjectId 长度不能超过 255")
    private String subjectId;

    /**
     * 主体关系
     */
    @Size(max = 64, message = "subjectRelation 长度不能超过 64")
    private String subjectRelation;
}
