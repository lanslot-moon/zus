package org.kitona.zus.domain.event;

import lombok.Getter;

/**
 * 模型更新事件
 * 
 * 当授权模型被创建、发布或废弃时触发此事件。
 * 用于通知相关组件刷新模型缓存。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public class ModelUpdatedEvent extends DomainEvent {

    /**
     * 事件类型常量
     */
    public static final String EVENT_TYPE = "model.updated";

    /**
     * 操作类型
     */
    public enum Operation {
        /**
         * 创建
         */
        CREATED,
        /**
         * 发布
         */
        PUBLISHED,
        /**
         * 废弃
         */
        DEPRECATED,
        /**
         * 删除
         */
        DELETED
    }

    /**
     * 模型ID
     */
    private final String modelId;

    /**
     * 操作类型
     */
    private final Operation operation;

    /**
     * 构造函数
     * 
     * @param storeId   存储空间ID
     * @param modelId   模型ID
     * @param operation 操作类型
     */
    public ModelUpdatedEvent(String storeId, String modelId, Operation operation) {
        super(storeId);
        this.modelId = modelId;
        this.operation = operation;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
