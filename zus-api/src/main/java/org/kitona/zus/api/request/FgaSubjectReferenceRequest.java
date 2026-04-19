package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 主体引用请求。
 */
@Data
public class FgaSubjectReferenceRequest {

    @NotBlank(message = "subject.type 不能为空")
    @Size(max = 64, message = "subject.type 长度不能超过 64")
    private String type;

    @NotBlank(message = "subject.id 不能为空")
    @Size(max = 128, message = "subject.id 长度不能超过 128")
    private String id;

    @Size(max = 64, message = "subject.relation 长度不能超过 64")
    private String relation;
}
