package org.kitona.zus.service.domain.leopard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
     * 集合计算结果
     */
    @Data
    @Builder
    public class ComputationResult {
        private String queryId;
        private Set<String> result;
        private Instant computedAt;
        private long computationTimeMs;
        private int totalRecords;
        private boolean fromCache;
        private String indexVersion;
        private Map<String, Object> queryMetadata;
    }
    