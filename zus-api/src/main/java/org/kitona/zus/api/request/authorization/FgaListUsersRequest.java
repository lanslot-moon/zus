package org.kitona.zus.api.request.authorization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;

import java.util.List;
import java.util.Map;

/**
 * FGA ListUsers 请求 —— 列出对某资源拥有指定关系的全部主体
 *
 * <p>典型用例：「谁有权编辑这篇文档」。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaListUsersRequest {

    /**
     * 资源引用
     */
    @Valid
    @NotNull(message = "object 不能为空")
    private FgaReferenceRequest object;

    /**
     * 关系名
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 主体类型过滤器（对齐 OpenFGA 的 userFilters）
     * <p>例如：返回「直接用户（{type: user}）」与「某些 group 的 member（{type: group, relation: member}）」。
     */
    @Valid
    @NotEmpty(message = "userFilters 不能为空")
    private List<UserFilter> userFilters;

    /**
     * 可选：模型版本
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 可选：一致性选项
     */
    @Valid
    private FgaConsistencyOptions consistency;

    /**
     * 可选：条件求值上下文
     */
    private Map<String, Object> context;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserFilter {
        /**
         * 主体类型
         */
        @NotBlank(message = "userFilter.type 不能为空")
        @Size(max = 64, message = "userFilter.type 长度不能超过 64")
        private String type;

        /**
         * 可选：userset 关系（如 member）
         */
        @Size(max = 64, message = "userFilter.relation 长度不能超过 64")
        private String relation;
    }
}
