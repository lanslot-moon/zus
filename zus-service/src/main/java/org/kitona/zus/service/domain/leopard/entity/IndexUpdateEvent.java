package org.kitona.zus.service.domain.leopard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
     * 索引更新事件
     */
    @Data
    @Builder
    public class IndexUpdateEvent {
        private String namespace;
        private String operation; // CREATE, UPDATE, DELETE
        private List<IndexRecord> affectedRecords;
        private Instant timestamp;
        private String source; // WATCH_API, MANUAL, BATCH
        private Map<String, Object> metadata;
    }
    