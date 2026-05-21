package org.kitona.zus.api.request.model;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 类型 Schema 输入。
 *
 * <p>类型名称由外层 {@code types} Map 的 key 提供，避免在 value 中重复传入 type 后形成双真相。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTypeSchemaInput {

    /**
     * 关系定义映射，key 为 relation name。
     */
    @Valid
    private Map<String, FgaRelationSchemaInput> relations;
}
