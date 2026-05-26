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
 * FGA Explain 请求 —— 调试一次 Check 的证明过程。
 *
 * <p>Explain 是独立冷路径入口，入参与 Check 保持一致，但响应会返回 resolution 树。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaExplainRequest {

    /**
     * 需要解释的授权三元组。
     */
    @Valid
    @NotNull(message = "tupleKey 不能为空")
    private FgaTupleKeyRequest tupleKey;

    /**
     * 指定用于 explain 的授权模型 ID，为空时使用 Store 当前激活模型。
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 条件求值所需的动态上下文。
     */
    private Map<String, Object> context;

    /**
     * Explain 请求的一致性选项。
     */
    @Valid
    private FgaConsistencyOptions consistency;
}
