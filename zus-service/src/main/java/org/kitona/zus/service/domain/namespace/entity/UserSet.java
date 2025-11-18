package org.kitona.zus.service.domain.namespace.entity;

import lombok.Builder;
import lombok.Data;

/**
     * 用户集
     */
    @Data
    @Builder
    class UserSet {
        private String type;                           // 类型: userset, computed_userset, tuple_to_userset
        private String value;                          // 值
        private String relation;                       // 关系（如果适用）
    }
    