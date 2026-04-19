package org.kitona.zus.api.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 类型限制输入 —— 对应数据库表 {@code fga_type_restriction}
 *
 * <p>表示某个 relation 允许哪些主体类型参与，对应 DSL 中
 * {@code define viewer: [user, group#member, user:*]} 方括号中的一项。
 *
 * <h3>三种表达</h3>
 * <ul>
 *   <li>直接主体：{@code {type: "user"}} → 允许 {@code user:alice} 等</li>
 *   <li>Userset：{@code {type: "group", relation: "member"}} → 允许 {@code group:eng#member}</li>
 *   <li>通配符：{@code {type: "user", wildcard: true}} → 允许 {@code user:*}</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTypeRestrictionInput {

    /**
     * 允许的主体类型
     */
    @NotBlank(message = "restriction.type 不能为空")
    @Size(max = 64, message = "restriction.type 长度不能超过 64")
    private String type;

    /**
     * 允许的主体关系（userset），如 {@code member}；直接用户或通配符留空。
     * <p>对应 {@code fga_type_restriction.allowed_subject_relation}（空值存储为 {@code ''}）。
     */
    @Size(max = 64, message = "restriction.relation 长度不能超过 64")
    private String relation;

    /**
     * 是否允许通配符授权（{@code user:*} 形式）。
     * <p>语义上等价于 OpenFGA DSL 中的 {@code user:*}。
     */
    private Boolean wildcard;

    /**
     * 可选：指定条件名，表示该限制项仅在条件满足时生效（with condition）。
     * <p>对应 OpenFGA DSL 的 {@code [user with is_working_hours]} 语法。
     */
    @Size(max = 64, message = "restriction.condition 长度不能超过 64")
    private String condition;
}
