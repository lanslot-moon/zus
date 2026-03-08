package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
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
    private String type;

    /**
     * 主体关系（用户集时使用）
     */
    private String relation;
}
