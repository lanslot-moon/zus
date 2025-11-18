package org.kitona.zus.service.listener.index;

import org.kitona.zus.service.domain.leopard.entity.IndexStats;
import org.kitona.zus.service.domain.leopard.entity.IndexUpdateEvent;

/**
 * 索引变更监听器
 */
public interface IndexChangeListener {
    void onIndexUpdate(IndexUpdateEvent event);

    void onIndexCreated(String namespace, IndexStats stats);

    void onIndexDeleted(String namespace);

    void onIndexOptimized(String namespace);

    void onError(String namespace, Throwable error);


}