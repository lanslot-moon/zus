package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 写入结果 VO
 *
 * <p>对应一次事务性 writes + deletes 的完成结果：返回新生成的 {@code zookie}，
 * 调用方可在后续 Check 中带入以保证「读到自己刚写」。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaWriteResultVO {

    /**
     * 本次写入产生的新 Zookie（对应 {@code fga_store.current_zookie}）
     */
    private String zookie;

    /**
     * 本次实际写入的元组数
     */
    private Integer writtenCount;

    /**
     * 本次实际删除的元组数
     */
    private Integer deletedCount;
}
