package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * FGA Check API 请求参数
 */
@Data
public class FgaCheckRequest {

    @Valid
    @NotNull(message = "object 不能为空")
    private FgaObjectReferenceRequest object;

    /**
     * 关系名称
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    @Valid
    @NotNull(message = "subject 不能为空")
    private FgaSubjectReferenceRequest subject;

    /**
     * 一致性令牌（Zookie）
     */
    private String consistencyToken;

    /**
     * 条件求值上下文。
     */
    private Map<String, Object> context;
}
