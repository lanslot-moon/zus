package org.kitona.zus.api.request.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 一致性选项 —— 对应 Zanzibar Zookie 一致性语义
 *
 * <p>对应数据库表：
 * <ul>
 *   <li>{@code fga_store.current_zookie} — Store 当前最新版本</li>
 *   <li>{@code fga_relation_tuple.zookie} — 每条元组的版本</li>
 * </ul>
 *
 * <p>语义说明：
 * <ul>
 *   <li>{@link Preference#MINIMIZE_LATENCY}：允许读从副本/缓存，可能略滞后，默认值</li>
 *   <li>{@link Preference#HIGHER_CONSISTENCY}：强一致，至少读到写侧最新版本</li>
 *   <li>{@link Preference#AT_LEAST_AS_FRESH}：须新于或等于 {@code atRevision} 指定的 zookie</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaConsistencyOptions {

    /**
     * 一致性偏好
     */
    private Preference preference;

    /**
     * 指定一致性基线 Zookie，仅 {@link Preference#AT_LEAST_AS_FRESH} 有效
     */
    private String atRevision;

    public enum Preference {
        MINIMIZE_LATENCY,
        HIGHER_CONSISTENCY,
        AT_LEAST_AS_FRESH
    }
}
