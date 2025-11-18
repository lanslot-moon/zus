package org.kitona.zus.service.domain.leopard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
     * 系统状态
     */
    @Data
    @Builder
    public class SystemStatus {
        private boolean healthy;
        private Instant lastHealthCheck;
        private int totalNamespaces;
        private long totalRecords;
        private long totalMemoryUsageBytes;
        private int activeQueries;
        private String version;
        private List<String> warnings;
        private List<String> errors;
        private Map<String, Object> performanceMetrics;
    }