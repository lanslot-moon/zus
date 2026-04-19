package org.kitona.zus.api.request.authorization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;

/**
 * FGA Expand 请求 —— 展开资源 / 关系背后的 userset 树
 *
 * <p>区别于 Check（回答「能/不能」），Expand 返回关系推导路径，供审计和调试：
 * <ul>
 *   <li>直接主体节点（leaf）</li>
 *   <li>用户集节点（userset，递归）</li>
 *   <li>计算集 / TTU 节点（按 rewrite 规则展开）</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaExpandRequest {

    /**
     * 要展开的资源
     */
    @Valid
    @NotNull(message = "object 不能为空")
    private FgaReferenceRequest object;

    /**
     * 要展开的关系
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

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
}
