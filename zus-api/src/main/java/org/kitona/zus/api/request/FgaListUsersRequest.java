package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    private String objectType;

    /**
     * 资源ID
     */
    @NotBlank(message = "objectId 不能为空")
    private String objectId;

    /**
     * 关系名称，如 viewer
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 用户类型过滤条件
     */
    @Valid
    private List<FgaUserFilter> userFilters;
}
