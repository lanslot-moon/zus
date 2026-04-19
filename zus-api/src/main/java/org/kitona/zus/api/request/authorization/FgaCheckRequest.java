package org.kitona.zus.api.request.authorization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;

import java.util.Map;

/**
 * FGA Check 请求 —— 检查主体对资源是否拥有指定关系
 *
 * <p>内部流程：
 * <ol>
 *   <li>加载模型（优先 {@link #authorizationModelId}，否则 Store 当前激活模型）</li>
 *   <li>按 rewrite 规则递归展开并匹配 {@code fga_relation_tuple}</li>
 *   <li>元组若绑定了 condition_definition_id，加载表达式 + {@link #context} 求值</li>
 *   <li>检查 {@code expires_at} 与 {@code is_wildcard}</li>
 *   <li>返回 allowed + 可选 resolution 树（仅当 {@link #trace} = true）</li>
 * </ol>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaCheckRequest {

    /**
     * 要检查的关系元组键
     */
    @Valid
    @NotNull(message = "tupleKey 不能为空")
    private FgaTupleKeyRequest tupleKey;

    /**
     * 可选：指定基于的模型版本（回归测试场景用）。为空则使用 Store 当前激活模型。
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 可选：条件求值运行时上下文（ABAC）
     */
    private Map<String, Object> context;

    /**
     * 可选：一致性选项
     */
    @Valid
    private FgaConsistencyOptions consistency;

    /**
     * 是否返回解析过程（调试用，性能略低）
     */
    private Boolean trace;
}
