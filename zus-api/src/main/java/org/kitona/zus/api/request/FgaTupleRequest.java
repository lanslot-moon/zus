package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
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
    private String objectType;

    /**
     * 资源ID
     */
    @NotBlank(message = "objectId 不能为空")
    private String objectId;

    /**
     * 关系名称
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 主体类型
     */
    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    /**
     * 主体关系
     */
    private String subjectRelation;
}
