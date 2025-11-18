package org.kitona.zus.service.domain.leopard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
     * 索引统计信息
     */
    @Data
    @Builder
    public class IndexStats {
        private String namespace;
        private long totalRecords;
        private long indexSizeBytes;
        private Instant lastUpdateTime;
        private long queryCount;
        private double averageQueryTimeMs;
        private long cacheHitCount;
        private long cacheMissCount;
        private String healthStatus;
        private int shardCount;
    }
    