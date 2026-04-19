package org.kitona.zus.api.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 元组读取中的资源过滤条件。
 */
@Data
public class FgaObjectFilterRequest {

    @Size(max = 64, message = "object.type 长度不能超过 64")
    private String type;

    @Size(max = 128, message = "object.id 长度不能超过 128")
    private String id;
}
