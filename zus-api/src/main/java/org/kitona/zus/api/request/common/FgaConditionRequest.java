package org.kitona.zus.api.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * FGA 条件引用（ABAC 混合模式）
 *
 * <p>用于：
 * <ol>
 *   <li><b>Write 元组</b>时绑定条件（写入 {@code fga_relation_tuple.condition_definition_id}
 *       + {@code condition_context}）；</li>
 *   <li><b>Check / ListObjects / ListUsers</b> 时提供运行时上下文，引擎在元组匹配后，
 *       以 {@code fga_condition_definition.expression} 结合 context 求值。</li>
 * </ol>
 *
 * <p>只引用条件的 {@code name}，具体表达式由 {@code fga_condition_definition} 管理；
 * 这避免元组冗余冻结表达式，保障条件可统一维护。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaConditionRequest {

    /**
     * 条件名，对应 {@code fga_condition_definition.condition_name}
     */
    @NotBlank(message = "condition.name 不能为空")
    @Size(max = 64, message = "condition.name 长度不能超过 64")
    private String name;

    /**
     * 条件求值上下文 —— 即 CEL 表达式中的 {@code input.params / input.context} 实参。
     * <p>写入时作为元组 {@code condition_context} 快照；查询时作为动态求值参数。
     */
    private Map<String, Object> context;
}
