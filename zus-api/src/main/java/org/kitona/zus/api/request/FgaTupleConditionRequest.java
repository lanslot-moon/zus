package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 元组条件绑定请求。
 *
 * <p>与 {@code fga_relation_tuple.condition_definition_id + condition_context} 对齐。
 */
@Data
public class FgaTupleConditionRequest {

    @NotNull(message = "condition.conditionDefinitionId 不能为空")
    private Long conditionDefinitionId;

    private Map<String, Object> context;
}
