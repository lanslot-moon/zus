package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建授权模型请求。
 *
 * <p>按照 schema-first 规范，模型写侧输入只接受结构化定义：
 * type、relation、type restriction 与 condition definition 都作为显式对象传入。
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
     * 类型定义列表。
     */
    @Valid
    @NotEmpty(message = "types 不能为空")
    private List<FgaTypeDefinitionInput> types;

    /**
     * 模型描述
     */
    @Size(max = 512, message = "description 长度不能超过 512")
    private String description;

    /**
     * 条件定义列表。
     */
    @Valid
    private List<FgaConditionDefinitionInput> conditions;
}
