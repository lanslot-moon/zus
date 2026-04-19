package org.kitona.zus.api.request.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FGA 关系定义输入 —— 对应数据库表 {@code fga_relation_definition}
 *
 * <p>一个 {@code FgaRelationDefinitionInput} 表示 DSL 中 {@code define xxx: ...} 一行，
 * 由 {@link #rewriteExpression} 描述权限推导规则，由 {@link #restrictions} 限制主体类型。
 *
 * <h3>{@code rewriteExpression} 支持的结构（对应 {@code relation_type}）</h3>
 * <ul>
 *   <li>{@code self}          — direct_only(0)，仅依赖直接元组</li>
 *   <li>{@code viewer}        — computed_userset(1)，等价于另一个关系</li>
 *   <li>{@code viewer from parent} — ttu(2)，tuple-to-userset</li>
 *   <li>{@code self or editor}、{@code self and required_clearance}、
 *       {@code self but not banned} — composite(3)，并 / 交 / 差组合</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaRelationDefinitionInput {

    /**
     * 关系名（如 viewer、editor、owner、parent、member）
     */
    @NotBlank(message = "name 不能为空")
    @Size(max = 64, message = "name 长度不能超过 64")
    private String name;

    /**
     * 重写表达式（默认 {@code self}，仅依赖直接元组）
     */
    @NotBlank(message = "rewriteExpression 不能为空")
    @Size(max = 512, message = "rewriteExpression 长度不能超过 512")
    private String rewriteExpression;

    /**
     * 类型限制列表 —— 可建立该关系的主体类型集合。
     * <p>对应数据库表 {@code fga_type_restriction}。
     * <p>示例：
     * <pre>
     *   [{type: "user"}, {type: "group", relation: "member"}, {type: "user", id: "*"}]
     * </pre>
     */
    @Valid
    private List<FgaTypeRestrictionInput> restrictions;
}
