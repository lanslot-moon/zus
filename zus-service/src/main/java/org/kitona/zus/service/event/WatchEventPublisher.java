package org.kitona.zus.service.event;

import org.kitona.zus.service.dto.response.WatchChangeResultDTO;

/**
 * Watch 事件发布接口
 *
 * <p>用于将元组变更事件推送给 Watch 订阅者。
 * <p>DDD 规范：接口定义在 Service 层，实现在 API 层（依赖倒置）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public interface WatchEventPublisher {

    /**
     * 发布变更事件到指定 Store 的所有订阅者
     *
     * @param storeId 存储空间ID
     * @param change  变更数据
     */
    void publish(String storeId, WatchChangeResultDTO change);
}
