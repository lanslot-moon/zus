package org.kitona.zus.service.event.application;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 应用事件基类
 *
 * <p>用于表达应用层在事务内完成核心状态变更后，对外发出的通知事实。
 */
@Getter
public abstract class ApplicationEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final String storeId;

    /**
     * 创建 ApplicationEvent 实例。
     *
     * @param storeId Store 标识
     */
    protected ApplicationEvent(String storeId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.storeId = storeId;
    }
}
