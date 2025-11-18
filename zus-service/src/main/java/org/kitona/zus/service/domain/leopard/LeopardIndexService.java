package org.kitona.zus.service.domain.leopard;

import org.kitona.zus.service.domain.leopard.entity.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Leopard索引系统
 * 负责优化大型嵌套集合的计算
 * 通过Watch API监听ACL数据变更
 */
@Component
public interface LeopardIndexService {

    /**
     * 初始化索引系统
     */
    void initialize();
    
    /**
     * 关闭索引系统
     */
    void shutdown();
    
    /**
     * 创建命名空间索引
     */
    CompletableFuture<IndexStats> createNamespaceIndex(String namespace);
    
    /**
     * 删除命名空间索引
     */
    CompletableFuture<Boolean> deleteNamespaceIndex(String namespace);
    
    /**
     * 执行集合计算查询
     */
    CompletableFuture<ComputationResult> computeSet(QueryRequest request);
    
    /**
     * 批量计算集合查询
     */
    CompletableFuture<List<ComputationResult>> computeBatchSets(List<QueryRequest> requests);
    
    /**
     * 更新索引
     */
    CompletableFuture<Boolean> updateIndex(String namespace, IndexRecord indexRecord);
    
    /**
     * 批量更新索引
     */
    CompletableFuture<Boolean> batchUpdateIndex(String namespace, List<IndexRecord> records);
    
    /**
     * 从变更日志重建索引
     */
    CompletableFuture<IndexStats> rebuildIndexFromChangeLog(String namespace, Instant fromTimestamp);
    
    /**
     * 获取索引统计信息
     */
    IndexStats getIndexStats(String namespace);
    
    /**
     * 获取所有命名空间的索引统计信息
     */
    Map<String, IndexStats> getAllIndexStats();
    
    /**
     * 检查索引是否存在
     */
    boolean indexExists(String namespace);
    
    /**
     * 获取索引健康状态
     */
    String getHealthStatus(String namespace);
    
    /**
     * 优化索引
     */
    CompletableFuture<Void> optimizeIndex(String namespace);
    
    /**
     * 清理过期索引
     */
    CompletableFuture<Integer> cleanupExpiredIndexes();
    
    /**
     * 获取查询历史
     */
    List<ComputationResult> getQueryHistory(String namespace, Instant fromTime, Instant toTime);
    
    /**
     * 清空查询缓存
     */
    void clearCache(String namespace);
    
    /**
     * 预热缓存
     */
    CompletableFuture<Void> warmupCache(String namespace);
    
    /**
     * 导出索引数据
     */
    CompletableFuture<String> exportIndex(String namespace, String format); // JSON, XML, CSV
    
    /**
     * 导入索引数据
     */
    CompletableFuture<Void> importIndex(String namespace, String data, String format);
    
    /**
     * 获取系统状态
     */
    SystemStatus getSystemStatus();
    

    

}