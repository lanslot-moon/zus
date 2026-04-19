package org.kitona.zus.api.request.tuple;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;

import java.util.List;

/**
 * FGA 写入请求 —— 事务性 writes + deletes
 *
 * <p>对应 OpenFGA {@code /write} 语义：本次调用内部全部成功或全部回滚，
 * 一次请求最多可混合写入与删除，原子写入 {@code fga_relation_tuple}
 * 并追加一行到 {@code fga_tuple_changelog}（每个操作一条日志）。
 *
 * <h3>关键字段</h3>
 * <ul>
 *   <li>{@link #writes}  — 插入 / upsert 元组（带 condition、expiresAt）</li>
 *   <li>{@link #deletes} — 按 tupleKey 精确删除（逻辑删除）</li>
 *   <li>{@link #authorizationModelId} — 可选，锁定本次写入基于的模型版本，
 *       服务端会校验元组的 type / relation 是否在该模型中存在</li>
 * </ul>
 *
 * <h3>审计元数据</h3>
 * 不在此对象中，由 HTTP Header 统一注入：
 * <pre>
 *   X-Fga-Operator-Id:  操作人标识
 *   X-Fga-Request-Id:   请求追踪 ID
 *   X-Fga-Source:       操作来源（API / SYNC / CLEANUP / MIGRATION）
 * </pre>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaWriteRequest {

    /**
     * 写入列表（插入或 upsert）
     */
    @Valid
    @Size(max = 100, message = "单次写入不能超过 100 条")
    private List<FgaTupleWriteItem> writes;

    /**
     * 删除列表（按 tupleKey 精确匹配）
     */
    @Valid
    @Size(max = 100, message = "单次删除不能超过 100 条")
    private List<FgaTupleKeyRequest> deletes;

    /**
     * 可选：指定本次写入所基于的模型版本
     * <p>为空时默认使用 Store 当前激活模型。
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 至少一种操作
     */
    @AssertTrue(message = "writes 与 deletes 至少一个非空")
    public boolean isNonEmpty() {
        return (writes != null && !writes.isEmpty()) || (deletes != null && !deletes.isEmpty());
    }
}
