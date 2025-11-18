package org.kitona.zus.service.domain.leopard;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Leopard索引配置
 */
@Data
@Builder
@Component
public class LeopardIndexConfig {
    
    /**
     * 索引存储路径
     */
    private String indexStoragePath;
    
    /**
     * 索引版本
     */
    @Builder.Default
    private String indexVersion = "1.0.0";
    
    /**
     * 最大索引大小（MB）
     */
    @Builder.Default
    private int maxIndexSizeMB = 1024;
    
    /**
     * 最小文档数阈值
     */
    @Builder.Default
    private int minDocumentThreshold = 1000;
    
    /**
     * 索引更新间隔（分钟）
     */
    @Builder.Default
    private int updateIntervalMinutes = 5;
    
    /**
     * 是否启用实时更新
     */
    @Builder.Default
    private boolean enableRealTimeUpdate = true;
    
    /**
     * 索引重建阈值（文档修改比例）
     */
    @Builder.Default
    private double rebuildThreshold = 0.3;
    
    /**
     * 并发索引线程数
     */
    @Builder.Default
    private int indexConcurrency = 4;
    
    /**
     * 索引缓存大小
     */
    @Builder.Default
    private int cacheSize = 10000;
    
    /**
     * 查询超时时间（秒）
     */
    @Builder.Default
    private int queryTimeoutSeconds = 30;
    
    /**
     * 是否启用压缩
     */
    @Builder.Default
    private boolean enableCompression = true;
    
    /**
     * 是否启用增量更新
     */
    @Builder.Default
    private boolean enableIncrementalUpdate = true;
    
    /**
     * 最大增量更新大小
     */
    @Builder.Default
    private int maxIncrementalUpdateSize = 1000;
    
    /**
     * 索引碎片大小（MB）
     */
    @Builder.Default
    private int shardSizeMB = 128;
    
    /**
     * 最大查询并发数
     */
    @Builder.Default
    private int maxQueryConcurrency = 100;
    
    /**
     * 预热缓存
     */
    @Builder.Default
    private boolean enableCacheWarmup = true;
    
    /**
     * 统计信息收集间隔（秒）
     */
    @Builder.Default
    private int statsCollectionIntervalSeconds = 60;
}