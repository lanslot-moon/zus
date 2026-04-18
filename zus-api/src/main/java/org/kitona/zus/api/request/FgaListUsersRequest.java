package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * FGA ListUsers API 请求
 */
@Data
public class FgaListUsersRequest {

    @Valid
    @NotNull(message = "object 不能为空")
    private FgaObjectReferenceRequest object;

    /**
     * 关系名称，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    @Valid
    private FgaSubjectFilterRequest subjectFilter;

    /**
     * 一致性令牌（Zookie）。
     */
    private String consistencyToken;

    /**
     * 条件求值上下文。
     */
    private Map<String, Object> context;
}
