//package org.kitona.zus.api.zanzibar.watch;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.kitona.zus.business.domain.request.WatchRequest;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//import reactor.core.publisher.Flux;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
///**
// * Watch Server - 专门处理实时ACL变更监听
// * 监听changelog数据库，向客户端提供近实时的命名空间变更流
// */
//@Slf4j
//@RestController
//@RequestMapping("/zanzibar/watch/v1")
//@RequiredArgsConstructor
//public class WatchServer {
//
//    private final WatchService watchService;
//    private final ChangeLogListener changeLogListener;
//
//    /**
//     * SSE (Server-Sent Events) 实时流监听
//     * 提供基于HTTP的实时变更通知
//     */
//    @GetMapping(value = "/{namespace}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter stream(
//            @PathVariable String namespace,
//            @RequestParam(value = "start_version", required = false) Long startVersion,
//            @RequestParam(value = "end_version", required = false) Long endVersion,
//            @RequestParam(value = "buffer_size", defaultValue = "1000") Integer bufferSize) {
//
//        log.info("[WATCH-SERVER] 创建SSE流: namespace={}, startVersion={}, endVersion={}",
//                namespace, startVersion, endVersion);
//
//        SseEmitter emitter = new SseEmitter(30000L); // 30秒超时
//
//        // 创建监听请求
//        WatchRequest request = WatchRequest.builder()
//                .namespace(namespace)
//                .startVersion(startVersion != null ? startVersion : System.currentTimeMillis())
//                .endVersion(endVersion)
//                .channelBufferSize(bufferSize)
//                .build();
//
//        // 启动实时监听
//        CompletableFuture<Void> watchFuture = watchService.startWatch(request, new WatchEventHandler() {
//            @Override
//            public void onChange(WatchResponse change) {
//                try {
//                    emitter.send(SseEmitter.event()
//                            .data(change)
//                            .id(change.getVersion().toString())
//                            .name(change.getOperation()));
//                } catch (Exception e) {
//                    log.warn("[WATCH-SERVER] 发送SSE事件失败: {}", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                try {
//                    emitter.completeWithError(error);
//                } catch (Exception e) {
//                    log.warn("[WATCH-SERVER] 完成SSE错误事件失败: {}", e.getMessage());
//                }
//            }
//
//            @Override
//            public void onComplete() {
//                try {
//                    emitter.complete();
//                } catch (Exception e) {
//                    log.warn("[WATCH-SERVER] 完成SSE事件失败: {}", e.getMessage());
//                }
//            }
//        });
//
//        // 设置回调
//        emitter.onTimeout(() -> {
//            log.info("[WATCH-SERVER] SSE流超时: namespace={}", namespace);
//            watchService.stopWatch(request.getNamespace());
//        });
//
//        emitter.onCompletion(() -> {
//            log.info("[WATCH-SERVER] SSE流完成: namespace={}", namespace);
//            watchService.stopWatch(request.getNamespace());
//        });
//
//        return emitter;
//    }
//
//    /**
//     * WebSocket 实时流监听
//     * 提供更灵活的实时双向通信
//     */
//    @GetMapping(value = "/{namespace}/websocket")
//    public CompletableFuture<String> createWebSocketSession(
//            @PathVariable String namespace,
//            @RequestParam(value = "start_version", required = false) Long startVersion,
//            @RequestParam(value = "buffer_size", defaultValue = "1000") Integer bufferSize) {
//
//        log.info("[WATCH-SERVER] 创建WebSocket会话: namespace={}, startVersion={}", namespace, startVersion);
//
//        String sessionId = generateSessionId();
//
//        WatchRequest request = WatchRequest.builder()
//                .namespace(namespace)
//                .startVersion(startVersion != null ? startVersion : System.currentTimeMillis())
//                .channelBufferSize(bufferSize)
//                .build();
//
//        // 创建WebSocket处理器
//        WebSocketHandler handler = new WebSocketHandler(sessionId);
//
//        return watchService.startWatch(request, handler)
//                .thenApply(v -> sessionId);
//    }
//
//    /**
//     * 批量轮询接口
//     * 客户端可以定期轮询获取变更
//     */
//    @GetMapping("/{namespace}/poll")
//    public CompletableFuture<ChangePollResponse> poll(
//            @PathVariable String namespace,
//            @RequestParam("start_version") Long startVersion,
//            @RequestParam(value = "end_version", required = false) Long endVersion,
//            @RequestParam(value = "max_changes", defaultValue = "100") Integer maxChanges) {
//
//        log.debug("[WATCH-SERVER] 批量轮询: namespace={}, startVersion={}, maxChanges={}",
//                namespace, startVersion, maxChanges);
//
//        WatchRequest request = WatchRequest.builder()
//                .namespace(namespace)
//                .startVersion(startVersion)
//                .endVersion(endVersion)
//                .build();
//
//        return watchService.pollChanges(request, maxChanges)
//                .thenApply(changes -> ChangePollResponse.builder()
//                        .changes(changes)
//                        .nextVersion(changes.stream()
//                                .mapToLong(WatchResponse::getVersion)
//                                .max()
//                                .orElse(startVersion))
//                        .build());
//    }
//
//    /**
//     * 获取最新版本号
//     */
//    @GetMapping("/{namespace}/latest-version")
//    public CompletableFuture<Long> getLatestVersion(@PathVariable String namespace) {
//        log.debug("[WATCH-SERVER] 获取最新版本: namespace={}", namespace);
//        return watchService.getLatestVersion(namespace);
//    }
//
//    /**
//     * 验证监听连接
//     */
//    @GetMapping("/{namespace}/heartbeat")
//    public CompletableFuture<HeartbeatResponse> heartbeat(
//            @PathVariable String namespace,
//            @RequestParam("session_id") String sessionId) {
//
//        log.debug("[WATCH-SERVER] 心跳检查: namespace={}, sessionId={}", namespace, sessionId);
//
//        return watchService.isSessionAlive(sessionId, namespace)
//                .thenApply(alive -> HeartbeatResponse.builder()
//                        .alive(alive)
//                        .timestamp(System.currentTimeMillis())
//                        .build());
//    }
//
//    /**
//     * 获取监听统计信息
//     */
//    @GetMapping("/{namespace}/stats")
//    public CompletableFuture<WatchStatsResponse> getStats(@PathVariable String namespace) {
//        log.debug("[WATCH-SERVER] 获取监听统计: namespace={}", namespace);
//        return watchService.getWatchStats(namespace);
//    }
//
//    private String generateSessionId() {
//        return "session_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
//    }
//
//    /**
//     * WebSocket事件处理器
//     */
//    private static class WebSocketHandler implements WatchEventHandler {
//        private final String sessionId;
//
//        public WebSocketHandler(String sessionId) {
//            this.sessionId = sessionId;
//        }
//
//        @Override
//        public void onChange(WatchResponse change) {
//            // 这里应该发送到WebSocket连接
//            log.info("[WATCH-HANDLER] WebSocket变更通知: sessionId={}, change={}", sessionId, change);
//        }
//
//        @Override
//        public void onError(Throwable error) {
//            log.error("[WATCH-HANDLER] WebSocket错误: sessionId={}, error={}", sessionId, error.getMessage(), error);
//        }
//
//        @Override
//        public void onComplete() {
//            log.info("[WATCH-HANDLER] WebSocket完成: sessionId={}", sessionId);
//        }
//    }
//}