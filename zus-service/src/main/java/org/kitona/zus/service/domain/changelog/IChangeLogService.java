package org.kitona.zus.service.domain.changelog;

import org.kitona.zus.service.domain.changelog.entity.ChangeLogQuery;
import org.kitona.zus.service.domain.changelog.entity.ChangeLogRecord;
import org.kitona.zus.service.domain.changelog.entity.ChangeLogStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 变更日志服务接口
 * 提供跨所有命名空间的共享变更日志管理功能
 */
public interface IChangeLogService {

    /**
     * 写入变更日志
     */
    CompletableFuture<ChangeLogRecord> writeChangeLog(ChangeLogRecord logRecord);
    
    /**
     * 批量写入变更日志
     */
    CompletableFuture<List<ChangeLogRecord>> writeChangeLogBatch(List<ChangeLogRecord> records);
    
    /**
     * 读取变更日志
     */
    CompletableFuture<List<ChangeLogRecord>> readChangeLog(ChangeLogQuery query);
    
    /**
     * 获取指定版本的变更日志
     */
    CompletableFuture<ChangeLogRecord> getChangeLogByVersion(Long version);
    
    /**
     * 获取最新变更日志
     */
    CompletableFuture<ChangeLogRecord> getLatestChangeLog();
    
    /**
     * 获取命名空间变更日志
     */
    CompletableFuture<List<ChangeLogRecord>> getNamespaceChanges(String namespace, Long fromVersion, int limit);
    
    /**
     * 获取变更日志统计信息
     */
    CompletableFuture<ChangeLogStats> getChangeLogStats();
    
    /**
     * 清理过期变更日志
     */
    CompletableFuture<Long> cleanupExpiredChangeLogs(LocalDateTime retentionTime);
    
    /**
     * 获取当前全局版本
     */
    CompletableFuture<Long> getCurrentGlobalVersion();
    
    /**
     * 获取下个全局版本
     */
    CompletableFuture<Long> getNextGlobalVersion();
    
    /**
     * 压缩变更日志（合并小文件，优化存储）
     */
    CompletableFuture<Void> compactChangeLog();
    
    /**
     * 导入变更日志（从外部源）
     */
    CompletableFuture<Long> importChangeLogs(List<ChangeLogRecord> records);
    
    /**
     * 导出变更日志（备份）
     */
    CompletableFuture<List<ChangeLogRecord>> exportChangeLogs(ChangeLogQuery query);
}