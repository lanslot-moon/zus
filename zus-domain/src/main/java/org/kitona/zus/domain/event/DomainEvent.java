package org.kitona.zus.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件基类
 * 
 * 所有 FGA 领域事件的抽象基类，定义了事件的公共属性。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public abstract class DomainEvent {

    /**
     * 事件唯一ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final Instant occurredAt;

    /**
     * 存储空间ID
     */
    private final String storeId;

    /**
     * 构造函数
     * 
     * @param storeId 存储空间ID
     */
    protected DomainEvent(String storeId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.storeId = storeId;
    }

    /**
     * 获取事件类型名称
     * 
     * @return 事件类型
     */
    public abstract String getEventType();
}
