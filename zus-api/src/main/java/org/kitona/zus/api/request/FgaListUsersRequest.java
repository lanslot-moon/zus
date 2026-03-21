package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA ListUsers API 请求
 */
@Data
public class FgaListUsersRequest {

    /**
     * 资源类型
     */
    @NotBlank(message = "objectType 不能为空")
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;

    /**
     * 资源ID
     */
    @NotBlank(message = "objectId 不能为空")
    @Size(max = 255, message = "objectId 长度不能超过 255")
    private String objectId;

    /**
     * 关系名称，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 用户类型过滤条件
     */
    @Valid
    private List<FgaUserFilter> userFilters;
}
