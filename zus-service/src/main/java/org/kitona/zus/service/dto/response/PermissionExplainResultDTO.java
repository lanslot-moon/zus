package org.kitona.zus.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限解释结果 DTO。
 */
@Data
@NoArgsConstructor
public class PermissionExplainResultDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 2862715460019756729L;

    /**
     * 本次 Explain 对应的最终授权结果。
     */
    private boolean allowed;

    /**
     * 本次 Explain 调用耗时，单位毫秒。
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
     * Explain 失败时的错误信息。
     */
    private String errorMessage;

    /**
     * 本次 Explain 的结构化解释结果。
     */
    private ExplainResolutionDTO resolution;

    /**
     * 创建正常 Explain 响应。
     *
     * @param allowed     是否允许
     * @param decision    鉴权语义结果
     * @param zookieToken 当前 zookie
     * @param durationMs  耗时
     * @param resolution  解释树
     * @return Explain 响应 DTO
     */
    public static PermissionExplainResultDTO of(boolean allowed, String decision, String zookieToken,
                                                long durationMs, ExplainResolutionDTO resolution) {
        PermissionExplainResultDTO dto = new PermissionExplainResultDTO();
        dto.allowed = allowed;
        dto.decision = decision;
        dto.zookieToken = zookieToken;
        dto.durationMs = durationMs;
        dto.resolution = resolution;
        return dto;
    }

    /**
     * 创建异常 Explain 响应。
     *
     * @param decision     异常语义结果
     * @param errorMessage 错误信息
     * @param durationMs   耗时
     * @return Explain 响应 DTO
     */
    public static PermissionExplainResultDTO error(String decision, String errorMessage, long durationMs) {
        PermissionExplainResultDTO dto = new PermissionExplainResultDTO();
        dto.allowed = false;
        dto.decision = decision;
        dto.errorMessage = errorMessage;
        dto.durationMs = durationMs;
        return dto;
    }
}
