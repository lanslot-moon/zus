package org.kitona.zus.service.domain.spanner.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Spanner配置
 */
@Data
@Builder
@Component
public class SpannerConfig {
    
    /**
     * Google Cloud项目ID
     */
    private String projectId;
    
    /**
     * Spanner实例ID
     */
    private String instanceId;
    
    /**
     * 数据库ID
     */
    private String databaseId;
    
    /**
     * 凭证文件路径
     */
    private String credentialsPath;
    
    /**
     * 最大连接数
     */
    @Builder.Default
    private int maxConnections = 100;
    
    /**
     * 最小连接数
     */
    @Builder.Default
    private int minConnections = 5;
    
    /**
     * 连接池大小
     */
    @Builder.Default
    private int poolSize = 25;
    
    /**
     * 会话过期时间（分钟）
     */
    @Builder.Default
    private int sessionTimeoutMinutes = 60;
    
    /**
     * 查询超时时间（秒）
     */
    @Builder.Default
    private int queryTimeoutSeconds = 30;
    
    /**
     * 批处理大小
     */
    @Builder.Default
    private int batchSize = 1000;
    
    /**
     * 是否启用压缩
     */
    @Builder.Default
    private boolean enableCompression = true;
    
    /**
     * 是否启用SSL
     */
    @Builder.Default
    private boolean enableSSL = true;
    
    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetries = 3;
    
    /**
     * 重试间隔（毫秒）
     */
    @Builder.Default
    private long retryDelayMs = 1000;
    
    /**
     * 事务重试次数
     */
    @Builder.Default
    private int transactionRetries = 3;
    
    /**
     * 是否启用读写分离
     */
    @Builder.Default
    private boolean enableReadWriteSplit = false;
    
    /**
     * 读取副本数量
     */
    @Builder.Default
    private int readReplicas = 1;
    
    /**
     * 健康检查间隔（秒）
     */
    @Builder.Default
    private int healthCheckIntervalSeconds = 30;
    
    /**
     * 监控指标收集间隔（秒）
     */
    @Builder.Default
    private int metricsIntervalSeconds = 60;
}