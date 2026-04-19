package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 类型限制 VO —— 对应 {@code fga_type_restriction}
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTypeRestrictionVO {

    /**
     * 允许的主体类型
     */
    private String type;

    /**
     * userset 关系（可空）
     */
    private String relation;

    /**
     * 是否通配符（{@code user:*}）
     */
    private Boolean wildcard;

    /**
     * 可选的条件名（with condition）
     */
    private String condition;
}
