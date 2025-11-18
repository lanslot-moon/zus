package org.kitona.zus.service.domain.namespace.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
     * 命名空间统计信息
     */
    @Data
    @Builder
    public class NamespaceStats {
        private String namespace;                      // 命名空间名称
        private long relationCount;                    // 关系数量
        private long tupleCount;                       // 元组数量
        private long changeLogEntries;                 // 变更日志条目数
        private Long lastModified;            // 最后修改时间
        private long storageSize;                      // 存储大小（字节）
        private Map<String, Long> relationStats;       // 各关系统计
    }