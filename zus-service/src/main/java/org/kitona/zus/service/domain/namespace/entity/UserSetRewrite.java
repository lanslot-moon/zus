package org.kitona.zus.service.domain.namespace.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
     * 用户集重写规则
     */
    @Data
    @Builder
    class UserSetRewrite {
        private String operation;                      // 操作类型: union, intersection, exclusion
        private List<UserSet> children;                // 子用户集
        private String description;                    // 描述
    }
    