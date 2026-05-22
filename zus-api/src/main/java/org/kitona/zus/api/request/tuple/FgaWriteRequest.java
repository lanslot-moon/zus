package org.kitona.zus.api.request.tuple;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FGA 写入请求。
 *
 * <p>该请求只承载写入 / upsert 元组列表。删除能力已经拆分到独立的
 * {@link FgaDeleteRequest}，避免一个 API 同时承担 writes 与 deletes 两种操作语义。
 *
 * <h3>关键字段</h3>
 * <ul>
 *   <li>{@link #writes}  — 插入 / upsert 元组（带 condition、expiresAt）</li>
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
     * 可选：指定本次写入所基于的模型版本
     * <p>为空时默认使用 Store 当前激活模型。
     */
    @Size(max = 64, message = "authorizationModelId 长度不能超过 64")
    private String authorizationModelId;

    /**
     * 至少包含一条写入操作。
     *
     * @return 写入列表非空返回 true
     */
    @AssertTrue(message = "writes 不能为空")
    public boolean isNonEmpty() {
        return writes != null && !writes.isEmpty();
    }
}
