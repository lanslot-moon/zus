package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA 创建存储空间请求
 */
@Data
public class FgaCreateStoreRequest {

    /**
     * 存储空间名称
     */
    @NotBlank(message = "name 不能为空")
    @Size(max = 64, message = "name 长度不能超过 64")
    private String name;

    /**
     * 存储空间描述
     */
    @Size(max = 512, message = "description 长度不能超过 512")
    private String description;
}
