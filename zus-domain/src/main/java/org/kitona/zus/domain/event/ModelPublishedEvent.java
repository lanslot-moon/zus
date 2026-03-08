package org.kitona.zus.domain.event;

import lombok.Getter;

/**
 * 模型发布事件
 *
 * <p>当授权模型被发布时触发此事件，用于异步更新 Store 的当前模型ID。
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>避免跨聚合事务，通过领域事件实现最终一致性</li>
 *   <li>模型发布后异步通知 Store 聚合更新当前模型引用</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Getter
public class ModelPublishedEvent extends DomainEvent {

    public static final String EVENT_TYPE = "model.published";

    private final String modelId;

    public ModelPublishedEvent(String storeId, String modelId) {
        super(storeId);
        this.modelId = modelId;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
