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
 * FGA 删除请求。
 *
 * <p>该请求只承载按 tuple key 精确删除的列表。删除能力与写入能力拆分为独立入口，
 * 避免一个 API 同时承担 writes 与 deletes 两种操作语义。
 *
 * @author kitona
 * @since 2026-05-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaDeleteRequest {

    /**
     * 删除列表（按 tupleKey 精确匹配）。
     */
    @Valid
    @Size(max = 100, message = "单次删除不能超过 100 条")
    private List<FgaTupleKeyRequest> deletes;

    /**
     * 至少包含一条删除操作。
     *
     * @return 删除列表非空返回 true
     */
    @AssertTrue(message = "deletes 不能为空")
    public boolean isNonEmpty() {
        return deletes != null && !deletes.isEmpty();
    }
}
