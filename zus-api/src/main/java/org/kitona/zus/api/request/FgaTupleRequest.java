package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 元组变更项请求。
 */
@Data
public class FgaTupleRequest {

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

    @Valid
    private FgaTupleConditionRequest condition;

    /**
     * 元组过期时间（毫秒时间戳），为空表示永不过期。
     */
    private Long expiresAt;
}
