package org.kitona.zus.service.domain.spanner;

import lombok.Data;
import lombok.Builder;

import java.util.List;

/**
 * Spanner数据库适配器接口
 */
public interface SpannerAdapter {
    
    /**
     * 存储适配器状态
     */
    @Data
    @Builder
    class StorageStatus {
        private boolean healthy;              // 是否健康
        private String version;              // Spanner版本
        private long lastConnectionCheck;    // 最后连接检查时间
        private int activeSessions;          // 活跃会话数
        private List<String> databases;      // 数据库列表
        private String status;               // 详细状态
    }
    
    /**
     * 数据库操作结果
     */
    @Data
    @Builder
    class DatabaseResult<T> {
        private boolean success;             // 操作是否成功
        private T data;                      // 结果数据
        private String errorMessage;         // 错误信息
        private long executionTime;          // 执行时间（毫秒）
        private String operationId;          // 操作ID
    }
    
    /**
     * 批量操作结果
     */
    @Data
    @Builder
    class BatchResult {
        private int totalOperations;         // 总操作数
        private int successfulOperations;    // 成功操作数
        private int failedOperations;        // 失败操作数
        private List<String> errors;         // 错误列表
        private long executionTime;          // 执行时间（毫秒）
    }
}