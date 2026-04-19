package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 资源对象引用请求。
 */
@Data
public class FgaObjectReferenceRequest {

    @NotBlank(message = "object.type 不能为空")
    @Size(max = 64, message = "object.type 长度不能超过 64")
    private String type;

    @NotBlank(message = "object.id 不能为空")
    @Size(max = 128, message = "object.id 长度不能超过 128")
    private String id;
}
