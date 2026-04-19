package org.kitona.zus.api.request.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 关系元组键 —— 唯一标识一条元组（不含条件、过期、审计）
 *
 * <p>对应数据库表 {@code fga_relation_tuple} 的 UNIQUE KEY：
 * {@code (store_id, object_type, object_id, relation, subject_type, subject_id, subject_relation)}。
 *
 * <p>该结构在 Write / Read / Check / Expand 全部接口中复用，保证 API 与 Service 层的类型一致性，
 * 避免重复定义与转换。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTupleKeyRequest {

    /**
     * 资源引用（relation 字段不应填）
     */
    @Valid
    @NotNull(message = "object 不能为空")
    private FgaReferenceRequest object;

    /**
     * 关系名
     */
    @NotBlank(message = "relation 不能为空")
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 主体引用（可含 relation 表示 userset）
     */
    @Valid
    @NotNull(message = "subject 不能为空")
    private FgaReferenceRequest subject;
}
