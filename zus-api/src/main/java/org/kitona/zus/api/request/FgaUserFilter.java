package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA 用户过滤器
 */
@Data
public class FgaUserFilter {

    /**
     * 主体类型，如 user
     */
    @NotBlank(message = "type 不能为空")
    @Size(max = 64, message = "type 长度不能超过 64")
    private String type;

    /**
     * 主体关系（用户集时使用）
     */
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;
}
