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
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;

import java.util.List;
import java.util.Map;

/**
 * FGA 批量 Check 请求 —— 单次 HTTP 调用完成多个权限判断
 *
 * <p>对齐 OpenFGA 的 BatchCheck API 设计：
 * <ul>
 *   <li>同一批次共用 {@code authorizationModelId} 与 {@link #consistency}；</li>
 *   <li>每项可独立携带 {@code correlationId} 方便调用方对账；</li>
 *   <li>每项可独立携带 {@code context}，用于按条件差异化求值。</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaBatchCheckRequest {

    /**
     * 批量检查项
     */
    @Valid
    @NotEmpty(message = "checks 不能为空")
    @Size(max = 100, message = "单批不能超过 100 项")
    private List<CheckItem> checks;

    /**
     * 可选：共享模型版本
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 可选：共享一致性选项
     */
    @Valid
    private FgaConsistencyOptions consistency;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckItem {
        /**
         * 调用方自定义的关联标识，用于和响应中的结果对应。
         */
        @NotBlank(message = "correlationId 不能为空")
        @Size(max = 64, message = "correlationId 长度不能超过 64")
        private String correlationId;

        /**
         * 要检查的元组键
         */
        @Valid
        @NotNull(message = "tupleKey 不能为空")
        private FgaTupleKeyRequest tupleKey;

        /**
         * 本项独立的条件上下文（覆盖批次默认值）
         */
        private Map<String, Object> context;
    }
}
