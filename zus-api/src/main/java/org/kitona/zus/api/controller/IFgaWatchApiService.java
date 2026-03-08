package org.kitona.zus.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * FGA Watch API（SSE 订阅权限变更）
 *
 * storeId 说明：来自 URL 路径 /fga/stores/{storeId}，由调用方传入。获取方式：先调用 POST /fga/stores 创建或
 * GET /fga/stores 列出，从响应中取得 storeId；或按业务约定使用。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaWatchApiService {

    /**
     * 订阅权限变更（SSE）
     *
     * @param storeId 存储空间ID，来自路径，由调用方传入
     * @param startAt 起始 Zookie，可选
     * @return SSE Emitter
     */
    @GetMapping(value = "/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter watch(@PathVariable String storeId, @RequestParam(value = "start_at", required = false) Long startAt);
}
