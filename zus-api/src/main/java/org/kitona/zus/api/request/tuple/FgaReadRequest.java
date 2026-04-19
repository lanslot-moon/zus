package org.kitona.zus.api.request.tuple;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaTupleKeyFilterRequest;

/**
 * FGA 读元组请求 —— 按 TupleKey 任意字段过滤，游标分页
 *
 * <p>用途：
 * <ul>
 *   <li>资源侧枚举：只给 object → 该资源的全部授权</li>
 *   <li>主体侧反查：只给 subject → 该主体的全部权限</li>
 *   <li>精确匹配：给全部字段 → 返回 0 或 1 条</li>
 * </ul>
 *
 * <p>注意：该接口不经过模型解析，直接返回直接存储的元组（不展开 userset / TTU）。
 * 需要递归展开请使用 {@code /expand}；需要权限判断请使用 {@code /check}。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaReadRequest {

    /**
     * 元组过滤条件（所有字段可空）
     */
    @Valid
    @NotNull(message = "tupleKey 不能为空；可传 {} 过滤空值")
    private FgaTupleKeyFilterRequest tupleKey;

    /**
     * 一致性选项
     */
    @Valid
    private FgaConsistencyOptions consistency;

    /**
     * 分页续传令牌（首页传 null）
     */
    @Size(max = 512, message = "pageToken 长度不能超过 512")
    private String pageToken;

    /**
     * 每页条数，默认 50
     */
    @Min(value = 1, message = "pageSize 必须大于 0")
    @Max(value = 1000, message = "pageSize 不能超过 1000")
    private Integer pageSize;
}
