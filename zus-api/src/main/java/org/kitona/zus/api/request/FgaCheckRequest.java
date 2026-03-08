package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * FGA Check API 请求参数
 */
@Data
public class FgaCheckRequest {

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

    /**
     * 一致性令牌（Zookie）
     */
    private String consistencyToken;
}
