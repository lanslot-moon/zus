package org.kitona.zus.api.event;

import jakarta.annotation.Resource;
import org.kitona.zus.api.converter.WatchEventConverter;
import org.kitona.zus.api.response.WatchChangeEventVO;
import org.kitona.zus.api.sse.SseConnectionManager;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.kitona.zus.service.event.WatchEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Watch 事件发布实现
 *
 * <p>将变更事件通过 SSE 推送给订阅者。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
@Component
public class WatchEventPublisherImpl implements WatchEventPublisher {

    @Resource
    private SseConnectionManager sseConnectionManager;

    @Override
    public void publish(String storeId, TupleChangeResultDTO change) {
        if (storeId == null || change == null) {
            return;
        }
        WatchChangeEventVO event = WatchEventConverter.toChangeEvent(change);
        if (event != null) {
            sseConnectionManager.broadcastChange(storeId, event);
        }
    }
}
