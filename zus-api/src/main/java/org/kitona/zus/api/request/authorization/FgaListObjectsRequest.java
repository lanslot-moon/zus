package org.kitona.zus.api.request.authorization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;

import java.util.Map;

/**
 * FGA ListObjects 请求 —— 列出某主体对某类型资源有指定关系的全部对象 id
 *
 * <p>典型用例：「我有权查看的所有文档」。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaListObjectsRequest {

    /**
     * 主体引用
     */
    @Valid
    @NotNull(message = "subject 不能为空")
    private FgaReferenceRequest subject;

    /**
     * 关系名
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 对象类型（如 document）
     */
    @NotBlank(message = "objectType 不能为空")
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;

    /**
     * 可选：模型版本
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 可选：一致性选项
     */
    @Valid
    private FgaConsistencyOptions consistency;

    /**
     * 可选：条件求值上下文
     */
    private Map<String, Object> context;
}
