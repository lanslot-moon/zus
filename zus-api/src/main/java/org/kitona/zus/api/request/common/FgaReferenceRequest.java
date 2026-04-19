package org.kitona.zus.api.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 通用对象引用（Object / Subject 共用）
 *
 * <p>对应 OpenFGA 中的 {@code Object} 与 {@code User} 概念统一抽象：
 * <ul>
 *   <li>作为 Object（资源）使用时，{@code relation} 必须为空；</li>
 *   <li>作为 Subject（主体）使用时，{@code relation} 可选，表示 userset（如 group:eng#member）；</li>
 *   <li>{@code id} 支持通配符 {@code "*"}，对应 {@code fga_relation_tuple.is_wildcard = 1}。</li>
 * </ul>
 *
 * <p>字符串化表示：
 * <pre>
 *   无 relation:   type:id          (如 user:alice、document:doc-1、user:*)
 *   有 relation:   type:id#relation (如 group:eng#member)
 * </pre>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaReferenceRequest {

    /**
     * 实体类型（对应 DSL 中的 type 声明）
     */
    @NotBlank(message = "type 不能为空")
    @Size(max = 64, message = "type 长度不能超过 64")
    private String type;

    /**
     * 实体标识，支持通配符 {@code *}
     */
    @NotBlank(message = "id 不能为空")
    @Size(max = 128, message = "id 长度不能超过 128")
    private String id;

    /**
     * Subject 侧使用的 userset 关系，形如 {@code group:eng#member} 的 {@code #member} 部分。
     * <p>Object 侧不得传入；直接用户传空字符串或 null。
     */
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 是否通配符引用（{@code id == "*"}）
     */
    public boolean isWildcard() {
        return "*".equals(id);
    }
}
