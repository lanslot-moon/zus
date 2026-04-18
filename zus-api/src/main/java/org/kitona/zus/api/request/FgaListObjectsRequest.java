package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * FGA ListObjects API 请求
 */
@Data
public class FgaListObjectsRequest {

    @Valid
    @NotNull(message = "subject 不能为空")
    private FgaSubjectReferenceRequest subject;

    /**
     * 要查询的关系名，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 资源类型过滤，如 document
     */
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;

    /**
     * 一致性令牌（Zookie）。
     */
    private String consistencyToken;

    /**
     * 条件求值上下文。
     */
    private Map<String, Object> context;
}
