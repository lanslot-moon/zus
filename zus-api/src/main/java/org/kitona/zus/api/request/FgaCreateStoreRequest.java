package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
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
    private String name;

    /**
     * 存储空间描述
     */
    private String description;
}
