package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 64, message = "subjectType 长度不能超过 64")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    @Size(max = 255, message = "subjectId 长度不能超过 255")
    private String subjectId;

    /**
     * 主体关系（用户集时使用）
     */
    @Size(max = 64, message = "subjectRelation 长度不能超过 64")
    private String subjectRelation;

    /**
     * 要查询的关系名，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 资源类型过滤，如 document
     */
    @NotBlank(message = "objectType 不能为空")
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;
}
