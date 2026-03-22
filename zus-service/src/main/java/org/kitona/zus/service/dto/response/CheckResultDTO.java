package org.kitona.zus.service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限检查结果 DTO
 *
 * 用于 Check API 的响应结果传输。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@NoArgsConstructor
public class CheckResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9119798751512644968L;
    /**
     * 是否允许
     */
    private boolean allowed;

    /**
     * 检查耗时（毫秒）
     */
    private long durationMs;

    /**
     * 当前 Zookie 令牌
     */
    private String zookieToken;

    /**
     * 鉴权语义结果
     */
    private String decision;

    /**
     * 错误信息（如果有）
     */
    private String errorMessage;

    /**
     * 创建允许响应
     *
     * @param zookieToken Zookie 令牌
     * @param durationMs  耗时
     * @return 响应 DTO
     */
    public static CheckResultDTO allowed(String zookieToken, long durationMs) {
        CheckResultDTO dto = new CheckResultDTO();
        dto.allowed = true;
        dto.zookieToken = zookieToken;
        dto.durationMs = durationMs;
        dto.decision = "ALLOWED";
        return dto;
    }

    /**
     * 创建拒绝响应
     *
     * @param zookieToken Zookie 令牌
     * @param durationMs  耗时
     * @return 响应 DTO
     */
    public static CheckResultDTO denied(String zookieToken, long durationMs) {
        CheckResultDTO dto = new CheckResultDTO();
        dto.allowed = false;
        dto.zookieToken = zookieToken;
        dto.durationMs = durationMs;
        dto.decision = "DENIED";
        return dto;
    }

    /**
     * 创建错误响应
     *
     * @param errorMessage 错误信息
     * @return 响应 DTO
     */
    public static CheckResultDTO error(String errorMessage) {
        CheckResultDTO dto = new CheckResultDTO();
        dto.allowed = false;
        dto.errorMessage = errorMessage;
        dto.decision = "SYSTEM_ERROR";
        return dto;
    }

    /**
     * 创建带明确语义的错误响应
     *
     * @param decision     语义结果
     * @param errorMessage 错误信息
     * @param zookieToken  Zookie 令牌
     * @param durationMs   耗时
     * @return 响应 DTO
     */
    public static CheckResultDTO error(String decision, String errorMessage, String zookieToken, long durationMs) {
        CheckResultDTO dto = new CheckResultDTO();
        dto.allowed = false;
        dto.decision = decision;
        dto.errorMessage = errorMessage;
        dto.zookieToken = zookieToken;
        dto.durationMs = durationMs;
        return dto;
    }

    /**
     * 是否有错误
     *
     * @return 有错误返回 true
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
