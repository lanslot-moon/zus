package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 批量 Check 结果 VO
 *
 * <p>以 {@code correlationId → FgaCheckResultVO} 的 Map 结构返回，方便调用方对账。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaBatchCheckResultVO {

    /**
     * 每项结果，key 为请求中指定的 correlationId
     */
    private Map<String, FgaCheckResultVO> results;

    /**
     * 批次整体耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 响应时 Store 的 Zookie
     */
    private String zookie;
}
