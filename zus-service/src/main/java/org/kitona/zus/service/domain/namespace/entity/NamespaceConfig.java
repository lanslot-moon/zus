package org.kitona.zus.service.domain.namespace.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
     * 命名空间配置
     */
    @Data
    @Builder
    public class NamespaceConfig {
        private String name;                           // 命名空间名称
        private String displayName;                    // 显示名称
        private String version;                        // 配置版本
        private Map<String, RelationDefinition> relations; // 关系定义
        private Map<String, String> metadata;          // 元数据
        private Long createdAt;               // 创建时间
        private Long updatedAt;               // 更新时间
        private boolean active;                        // 是否激活
    }