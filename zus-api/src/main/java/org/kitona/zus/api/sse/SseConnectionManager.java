package org.kitona.zus.api.sse;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.response.WatchChangeEventVO;
import org.kitona.zus.api.response.WatchConnectedEventVO;
import org.kitona.zus.api.response.WatchHeartbeatEventVO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE 连接管理器
 *
 * <p>负责 SSE 连接的生命周期管理、心跳发送和事件广播。
 * <p>这是技术基础设施组件，不属于业务逻辑层。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
@Slf4j
@Component
public class SseConnectionManager {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;
    private static final long HEARTBEAT_INTERVAL = 30 * 1000L;

    private final Map<String, SseEmitter> activeEmitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 创建并注册 SSE 连接
     *
     * @param storeId 存储空间ID
     * @param startAt 起始时间戳
     * @return SseEmitter 实例
     */
    public SseEmitter createConnection(String storeId, Long startAt) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        String emitterId = storeId + "-" + System.currentTimeMillis();
        activeEmitters.put(emitterId, emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE 连接完成: emitterId={}", emitterId);
            activeEmitters.remove(emitterId);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE 连接超时: emitterId={}", emitterId);
            activeEmitters.remove(emitterId);
        });
        emitter.onError(e -> {
            log.warn("SSE 连接错误: emitterId={}, error={}", emitterId, e.getMessage());
            activeEmitters.remove(emitterId);
        });

        sendConnectedEvent(emitter, storeId, startAt);
        scheduleHeartbeat(emitter, emitterId);

        return emitter;
    }

    /**
     * 发送变更事件到指定 Store 的所有订阅者
     */
    public void broadcastChange(String storeId, WatchChangeEventVO event) {
        activeEmitters.forEach((emitterId, emitter) -> {
            if (emitterId.startsWith(storeId + "-")) {
                try {
                    emitter.send(SseEmitter.event().name("change").data(event));
                } catch (IOException e) {
                    log.warn("发送变更事件失败: emitterId={}", emitterId);
                    activeEmitters.remove(emitterId);
                }
            }
        });
    }

    /**
     * 发送变更事件列表
     */
    public void sendChangeEvents(SseEmitter emitter, java.util.List<WatchChangeEventVO> events) {
        for (WatchChangeEventVO event : events) {
            try {
                emitter.send(SseEmitter.event().name("change").data(event));
            } catch (IOException e) {
                log.warn("发送变更事件失败", e);
                break;
            }
        }
    }

    /**
     * 获取活跃连接总数
     */
    public int getActiveConnectionCount() {
        return activeEmitters.size();
    }

    /**
     * 获取指定 Store 的活跃连接数
     */
    public long getActiveConnectionCount(String storeId) {
        return activeEmitters.keySet().stream()
                .filter(id -> id.startsWith(storeId + "-"))
                .count();
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        activeEmitters.values().forEach(SseEmitter::complete);
        activeEmitters.clear();
    }

    private void sendConnectedEvent(SseEmitter emitter, String storeId, Long startAt) {
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(new WatchConnectedEventVO(storeId, startAt)));
        } catch (IOException e) {
            log.error("发送连接事件失败", e);
        }
    }

    private void scheduleHeartbeat(SseEmitter emitter, String emitterId) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!activeEmitters.containsKey(emitterId)) {
                return;
            }
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data(new WatchHeartbeatEventVO(System.currentTimeMillis())));
            } catch (IOException e) {
                log.debug("发送心跳失败，移除连接: emitterId={}", emitterId);
                activeEmitters.remove(emitterId);
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }
}
