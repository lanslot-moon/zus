package org.kitona.zus.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * FGA Watch API —— SSE 推模式订阅权限变更
 *
 * <p>与 {@code POST /read}（元组快照）、{@code GET /changes}（拉式变更）互补。
 * 对应数据库表：{@code fga_tuple_changelog}（按 {@code zookie} 游标追增量）。
 *
 * <p>SSE 事件类型：
 * <ul>
 *   <li>{@code connected}   连接建立，返回当前 Zookie 水位</li>
 *   <li>{@code tuple-change} 单条元组变更</li>
 *   <li>{@code heartbeat}   心跳，用于保活与断线检测</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaWatchApiService {

    /**
     * 订阅变更流（SSE）
     *
     * @param storeId Store 标识
     * @param startAt 起始 Zookie（不含），为空则从连接建立时刻起
     * @param type    可选：按 object.type 过滤
     */
    @GetMapping(value = "/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter watch(@PathVariable String storeId, @RequestParam(value = "start_at", required = false) Long startAt,
                     @RequestParam(value = "type", required = false) String type);
}
