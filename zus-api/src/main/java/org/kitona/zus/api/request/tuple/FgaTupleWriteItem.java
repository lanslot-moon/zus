package org.kitona.zus.api.request.tuple;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConditionRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;

/**
 * FGA 写入元组项 —— 元组键 + 可选条件绑定 + 可选过期时间
 *
 * <p>对应数据库表 {@code fga_relation_tuple} 一行写入所需的全部业务字段：
 * <ul>
 *   <li>{@link #tupleKey}    → object_type / object_id / relation / subject_type / subject_id / subject_relation + is_wildcard(推断)</li>
 *   <li>{@link #condition}   → condition_definition_id（按 name 反查）/ condition_name / condition_context</li>
 *   <li>{@link #expiresAt}   → expires_at（支持临时授权）</li>
 * </ul>
 * <p>{@code zookie} 由服务端分配，不接受客户端传入。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTupleWriteItem {

    /**
     * 元组键
     */
    @Valid
    @NotNull(message = "tupleKey 不能为空")
    private FgaTupleKeyRequest tupleKey;

    /**
     * 可选：ABAC 条件绑定
     */
    @Valid
    private FgaConditionRequest condition;

    /**
     * 可选：过期时间（毫秒时间戳）。{@code null} 表示永不过期。
     * <p>对应 {@code fga_relation_tuple.expires_at}。
     */
    private Long expiresAt;
}
