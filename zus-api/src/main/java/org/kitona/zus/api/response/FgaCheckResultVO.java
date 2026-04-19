package org.kitona.zus.api.response;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;

/**
 * FGA 权限检查结果 VO
 *
 * API 层返回对象，与应用层 DTO 解耦。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = PermissionCheckResultDTO.class)
public class FgaCheckResultVO {

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
     * 是否有错误
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
