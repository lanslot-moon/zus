package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FGA ListObjects API 请求
 */
@Data
public class FgaListObjectsRequest {

    /**
     * 主体类型，如 user
     */
    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    /**
     * 主体关系（用户集时使用）
     */
    private String subjectRelation;

    /**
     * 要查询的关系名，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 资源类型过滤，如 document
     */
    @NotBlank(message = "objectType 不能为空")
    private String objectType;
}
