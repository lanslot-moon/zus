package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 元组变更 VO —— 对应 {@code fga_tuple_changelog} 一行
 *
 * <p>用于 {@code /changes}（拉模式）与 {@code /watch}（SSE）返回变更事件。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTupleChangeVO {

    /**
     * 变更版本（zookie）
     */
    private String zookie;

    /**
     * 操作类型：{@code WRITE} / {@code DELETE}
     */
    private String operation;

    /**
     * 涉及的元组
     */
    private FgaTupleVO tuple;

    /**
     * 操作时间（毫秒时间戳）
     */
    private Long operationTime;

    /**
     * 操作人
     */
    private String operatorId;

    /**
     * 请求 ID
     */
    private String requestId;

    /**
     * 操作来源（API / SYNC / CLEANUP / MIGRATION）
     */
    private String source;
}
