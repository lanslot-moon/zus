package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA Explain 响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaExplainResultVO {

    /**
     * 本次 Explain 对应的最终授权结果。
     */
    private boolean allowed;

    /**
     * Explain 接口处理耗时，单位毫秒。
     */
    private long durationMs;

    /**
     * 本次 Explain 使用或返回的一致性 zookie。
     */
    private String zookieToken;

    /**
     * 本次 Explain 的决策编码。
     */
    private String decision;

    /**
     * Explain 失败时返回的错误信息。
     */
    private String errorMessage;

    /**
     * 本次 Explain 的结构化 resolution。
     */
    private FgaExplainResolutionVO resolution;
}
