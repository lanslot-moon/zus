package org.kitona.zus.service.domain.namespace.entity;

import lombok.Builder;
import lombok.Data;

import javax.management.relation.RelationType;
import java.util.List;
import java.util.Map;

/**
     * 关系定义
     */
    @Data
    @Builder
    class RelationDefinition {
        private String name;                           // 关系名称
        private String displayName;                    // 显示名称
        private RelationType type;                     // 关系类型
        private List<String> allowedTypes;             // 允许的对象类型
        private List<UserSetRewrite> rewriteRules;     // 重写规则
        private boolean writeable;                     // 是否可写
        private boolean readable;                      // 是否可读
        private Map<String, String> metadata;          // 元数据
    }