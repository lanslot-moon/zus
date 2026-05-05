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

    @Valid
    @NotNull(message = "tupleKey 不能为空")
    private FgaTupleKeyRequest tupleKey;

    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    private Map<String, Object> context;

    @Valid
    private FgaConsistencyOptions consistency;
}
