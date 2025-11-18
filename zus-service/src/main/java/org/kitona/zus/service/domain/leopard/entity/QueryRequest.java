package org.kitona.zus.service.domain.leopard.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
     * 查询参数
     */
    @Data
    @Builder
    public class QueryRequest {
        private String namespace;
        private String objectId;
        private String relation;
        private Set<String> subjectFilters;
        private boolean useCache;
        private long maxWaitTimeMs;
        private Map<String, Object> queryOptions;
    }