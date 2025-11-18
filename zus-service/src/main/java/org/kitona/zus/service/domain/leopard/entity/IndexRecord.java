package org.kitona.zus.service.domain.leopard.entity;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
     * 索引记录
     */
    @Data
    @Builder
    public class IndexRecord {
        private String namespace;
        private String objectId;
        private String relation;
        private Set<String> subjects;
        private Instant lastModified;
        private long version;
        private Map<String, Object> metadata;
    }
    