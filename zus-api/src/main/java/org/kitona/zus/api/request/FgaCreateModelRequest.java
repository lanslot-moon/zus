package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * FGA 创建授权模型请求
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
public class FgaCreateModelRequest {

    /**
     * 模型 Schema 版本，如 1.1
     */
    private String schemaVersion;

    /**
     * 类型定义列表
     */
    private List<FgaTypeDefinitionInput> typeDefinitions;

    /**
     * 授权模型 DSL 文本（与 typeDefinitions 二选一）
     */
    private String dslText;

    /**
     * 模型描述
     */
    @Size(max = 255, message = "description 长度不能超过 255")
    private String description;
}
