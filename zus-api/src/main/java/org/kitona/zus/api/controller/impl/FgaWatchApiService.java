package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaWatchApiService;
import org.kitona.zus.api.converter.WatchEventConverter;
import org.kitona.zus.api.response.WatchChangeEventVO;
import org.kitona.zus.api.sse.SseConnectionManager;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * FGA Watch API 实现
 *
 * <p>负责 HTTP 协议适配，SSE 连接管理委托给 {@link SseConnectionManager}。
 * <p>实时变更推送通过 {@link org.kitona.zus.api.event.WatchEventPublisherImpl} 实现。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaWatchApiService implements IFgaWatchApiService {

    private static final int HISTORICAL_CHANGE_LIMIT = 100;

    @Resource
    private ITupleWatchApplicationService watchApplicationService;

    @Resource
    private SseConnectionManager sseConnectionManager;

    @Override
    public SseEmitter watch(String storeId, Long startAt) {
        log.info("FgaWatchApiService watch, storeId:{}, startAt:{}", storeId, startAt);

        SseEmitter emitter = sseConnectionManager.createConnection(storeId, startAt);

        if (startAt != null) {
            List<TupleChangeResultDTO> changes =
                    watchApplicationService.getChanges(storeId, startAt, HISTORICAL_CHANGE_LIMIT);
            List<WatchChangeEventVO> events = WatchEventConverter.toChangeEventList(changes);
            sseConnectionManager.sendChangeEvents(emitter, events);
        }

        return emitter;
    }
}
