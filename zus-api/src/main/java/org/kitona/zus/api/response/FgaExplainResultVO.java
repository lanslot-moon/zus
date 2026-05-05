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

    private boolean allowed;
    private long durationMs;
    private String zookieToken;
    private String decision;
    private String errorMessage;
    private FgaExplainResolutionVO resolution;
}
