package org.kitona.zus.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/*
 * Title: IFgaWatchApiService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/18 13:57
 * Description: xxx
 */
public interface IFgaWatchApiService {

    /**
     * SSE (Server-Sent Events) 实时流监听
     * 提供基于HTTP的实时变更通知
     */
    @GetMapping(value = "/{namespace}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter stream(@PathVariable String namespace,
                      @RequestParam(value = "start_version", required = false) Long startVersion,
                      @RequestParam(value = "end_version", required = false) Long endVersion,
                      @RequestParam(value = "buffer_size", defaultValue = "1000") Integer bufferSize);
}
