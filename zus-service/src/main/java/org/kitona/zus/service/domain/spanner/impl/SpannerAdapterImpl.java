//package org.kitona.zus.service.domain.spanner.impl;
//
//import com.google.cloud.spanner.*;
//import com.google.cloud.spanner.admin.database.v1.DatabaseAdminClient;
//import com.google.api.core.ApiFuture;
//import com.google.api.gax.core.CredentialsProvider;
//import com.google.api.gax.core.NoCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.NoCredentials;
//import com.google.cloud.spanner.admin.database.v1.DatabaseAdminSettings;
//import lombok.extern.slf4j.Slf4j;
//import org.kitona.zus.storage.zanzibar.spanner.SpannerAdapter;
//import org.kitona.zus.storage.zanzibar.spanner.SpannerAdapter.*;
//import org.kitona.zus.storage.zanzibar.spanner.config.SpannerConfig;
//import org.kitona.zus.storage.zanzibar.namespace.NamespaceService;
//import org.kitona.zus.storage.zanzibar.changelog.ChangeLogService;
//import org.kitona.zus.storage.zanzibar.tuple.TupleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// * Spanner适配器实现
// */
//@Slf4j
//@Component
//public class SpannerAdapterImpl implements SpannerAdapter {
//
//    @Autowired
//    private SpannerConfig spannerConfig;
//
//    @Autowired
//    private NamespaceService namespaceService;
//
//    @Autowired
//    private ChangeLogService changeLogService;
//
//    @Autowired
//    private TupleService tupleService;
//
//    private Spanner spanner;
//    private DatabaseAdminClient databaseAdminClient;
//    private Database db;
//
//    // 健康检查相关
//    private final AtomicBoolean healthy = new AtomicBoolean(false);
//    private final AtomicLong lastHealthCheck = new AtomicLong(0);
//    private final AtomicInteger healthCheckFailures = new AtomicInteger(0);
//
//    // 统计信息
//    private final AtomicLong totalRequests = new AtomicLong(0);
//    private final AtomicLong successfulRequests = new AtomicLong(0);
//    private final AtomicLong failedRequests = new AtomicLong(0);
//    private final AtomicLong totalQueryTime = new AtomicLong(0);
//
//    // 线程池
//    private ExecutorService executorService;
//    private ScheduledExecutorService healthCheckScheduler;
//
//    @PostConstruct
//    public void initialize() {
//        log.info("正在初始化Spanner适配器...");
//        try {
//            // 创建线程池
//            executorService = Executors.newFixedThreadPool(10);
//            healthCheckScheduler = Executors.newScheduledThreadPool(2);
//
//            // 创建Spanner客户端
//            SpannerOptions.Builder builder = SpannerOptions.newBuilder();
//
//            // 设置凭证
//            if (spannerConfig.getCredentialsPath() != null && !spannerConfig.getCredentialsPath().isEmpty()) {
//                try (FileInputStream fis = new FileInputStream(spannerConfig.getCredentialsPath())) {
//                    GoogleCredentials credentials = GoogleCredentials.fromStream(fis);
//                    builder.setCredentials(credentials);
//                }
//            } else {
//                builder.setCredentialsProvider(new NoCredentialsProvider());
//            }
//
//            // 构建Spanner客户端
//            spanner = builder.build().getService();
//
//            // 创建数据库客户端
//            DatabaseId databaseId = DatabaseId.of(
//                spannerConfig.getProjectId(),
//                spannerConfig.getInstanceId(),
//                spannerConfig.getDatabaseId()
//            );
//            db = spanner.getDatabaseClient(databaseId);
//
//            // 创建数据库管理员客户端
//            DatabaseAdminSettings adminSettings = DatabaseAdminSettings.newBuilder()
//                .setCredentialsProvider(builder.getCredentialsProvider())
//                .build();
//            databaseAdminClient = DatabaseAdminClient.create(adminSettings);
//
//            // 验证连接
//            validateConnection();
//
//            // 启动健康检查
//            startHealthCheck();
//
//            healthy.set(true);
//            log.info("Spanner适配器初始化完成");
//
//        } catch (Exception e) {
//            log.error("Spanner适配器初始化失败", e);
//            throw new RuntimeException("Spanner适配器初始化失败", e);
//        }
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        log.info("正在关闭Spanner适配器...");
//
//        // 关闭线程池
//        if (executorService != null && !executorService.isShutdown()) {
//            executorService.shutdown();
//        }
//
//        if (healthCheckScheduler != null && !healthCheckScheduler.isShutdown()) {
//            healthCheckScheduler.shutdown();
//        }
//
//        // 关闭Spanner客户端
//        if (spanner != null) {
//            spanner.close();
//        }
//
//        healthy.set(false);
//        log.info("Spanner适配器已关闭");
//    }
//
//    @Override
//    public StorageStatus getStatus() {
//        long lastCheck = lastHealthCheck.get();
//        boolean isHealthy = healthy.get();
//
//        return StorageStatus.builder()
//            .healthy(isHealthy)
//            .lastHealthCheck(Instant.ofEpochMilli(lastCheck))
//            .version("1.0.0")
//            .uptime(getUptime())
//            .connectionCount(getActiveConnectionCount())
//            .pendingRequests(getPendingRequestCount())
//            .build();
//    }
//
//    @Override
//    public String getVersion() {
//        return "1.0.0";
//    }
//
//    @Override
//    public Instant getLastConnectionCheck() {
//        return Instant.ofEpochMilli(lastHealthCheck.get());
//    }
//
//    @Override
//    public DatabaseResult<Void> executeQuery(String sql, List<Value> params) {
//        return executeQuery(sql, params, QueryOptions.getDefaultInstance());
//    }
//
//    @Override
//    public DatabaseResult<Struct> executeQuery(String sql, List<Value> params, QueryOptions options) {
//        long startTime = System.currentTimeMillis();
//        totalRequests.incrementAndGet();
//
//        try {
//            Statement statement = Statement.newBuilder(sql);
//            if (params != null) {
//                for (Value param : params) {
//                    statement.bind(param.getColumnName())
//                        .to(param.getValue(), param.getType());
//                }
//            }
//
//            ResultSet resultSet = db.executeQuery(statement.build(), options);
//            List<Struct> results = new ArrayList<>();
//
//            while (resultSet.next()) {
//                results.add(resultSet.getCurrentRowAsStruct());
//            }
//
//            resultSet.close();
//
//            successfulRequests.incrementAndGet();
//            totalQueryTime.addAndGet(System.currentTimeMillis() - startTime);
//
//            return DatabaseResult.<Struct>builder()
//                .success(true)
//                .data(results)
//                .queryTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .rowCount(results.size())
//                .build();
//
//        } catch (Exception e) {
//            failedRequests.incrementAndGet();
//            log.error("执行查询失败: {}", sql, e);
//
//            return DatabaseResult.<Struct>builder()
//                .success(false)
//                .error(e.getMessage())
//                .queryTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .build();
//        }
//    }
//
//    @Override
//    public BatchResult executeBatchWrite(List<Mutation> mutations) {
//        return executeBatchWrite(mutations, spannerConfig.getBatchSize());
//    }
//
//    @Override
//    public BatchResult executeBatchWrite(List<Mutation> mutations, int batchSize) {
//        long startTime = System.currentTimeMillis();
//        totalRequests.incrementAndGet();
//
//        try {
//            BatchWriteException batchException = null;
//            int successCount = 0;
//            int failureCount = 0;
//
//            List<List<Mutation>> batches = createBatches(mutations, batchSize);
//
//            for (List<Mutation> batch : batches) {
//                try {
//                    db.write(batch);
//                    successCount += batch.size();
//                } catch (BatchWriteException e) {
//                    failureCount += e.getFailedMutations().size();
//                    successCount += batch.size() - e.getFailedMutations().size();
//                    batchException = e;
//
//                    log.warn("批次写入部分失败: 成功 {} 条, 失败 {} 条",
//                        successCount, failureCount);
//                    break; // 遇到失败就停止
//                }
//            }
//
//            successfulRequests.addAndGet(successCount);
//            if (failureCount > 0) {
//                failedRequests.addAndGet(failureCount);
//            }
//
//            return BatchResult.builder()
//                .success(failureCount == 0)
//                .successCount(successCount)
//                .failureCount(failureCount)
//                .processingTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .exception(batchException)
//                .build();
//
//        } catch (Exception e) {
//            failedRequests.incrementAndGet();
//            log.error("批量写入失败", e);
//
//            return BatchResult.builder()
//                .success(false)
//                .successCount(0)
//                .failureCount(mutations.size())
//                .processingTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .error(e.getMessage())
//                .exception(e)
//                .build();
//        }
//    }
//
//    @Override
//    public DatabaseResult<Long> executeRead(String table, String key, List<String> columns) {
//        long startTime = System.currentTimeMillis();
//        totalRequests.incrementAndGet();
//
//        try {
//            KeySet keySet = KeySet.newBuilder()
//                .addKey(Key.of(key))
//                .build();
//
//            ResultSet resultSet = db.read(table, keySet, columns);
//            Long count = 0L;
//
//            while (resultSet.next()) {
//                count++;
//            }
//
//            resultSet.close();
//
//            successfulRequests.incrementAndGet();
//            totalQueryTime.addAndGet(System.currentTimeMillis() - startTime);
//
//            return DatabaseResult.<Long>builder()
//                .success(true)
//                .data(Collections.singletonList(count))
//                .queryTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .rowCount(count.intValue())
//                .build();
//
//        } catch (Exception e) {
//            failedRequests.incrementAndGet();
//            log.error("执行读取失败: table={}, key={}", table, key, e);
//
//            return DatabaseResult.<Long>builder()
//                .success(false)
//                .error(e.getMessage())
//                .queryTime(Duration.ofMillis(System.currentTimeMillis() - startTime))
//                .build();
//        }
//    }
//
//    /**
//     * 验证数据库连接
//     */
//    private void validateConnection() throws Exception {
//        String sql = "SELECT 1";
//        DatabaseResult<Struct> result = executeQuery(sql, null);
//
//        if (!result.isSuccess()) {
//            throw new RuntimeException("数据库连接验证失败: " + result.getError());
//        }
//
//        log.info("数据库连接验证成功");
//    }
//
//    /**
//     * 启动健康检查
//     */
//    private void startHealthCheck() {
//        healthCheckScheduler.scheduleAtFixedRate(this::performHealthCheck,
//            0, spannerConfig.getHealthCheckIntervalSeconds(), TimeUnit.SECONDS);
//    }
//
//    /**
//     * 执行健康检查
//     */
//    private void performHealthCheck() {
//        try {
//            validateConnection();
//            lastHealthCheck.set(System.currentTimeMillis());
//            healthCheckFailures.set(0);
//            healthy.set(true);
//
//        } catch (Exception e) {
//            int failures = healthCheckFailures.incrementAndGet();
//            log.warn("健康检查失败 ({}): {}", failures, e.getMessage());
//
//            if (failures >= 3) {
//                healthy.set(false);
//                log.error("健康检查连续失败超过3次，标记为不健康");
//            }
//        }
//    }
//
//    /**
//     * 创建批次
//     */
//    private <T> List<List<T>> createBatches(List<T> items, int batchSize) {
//        List<List<T>> batches = new ArrayList<>();
//
//        for (int i = 0; i < items.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, items.size());
//            batches.add(items.subList(i, end));
//        }
//
//        return batches;
//    }
//
//    /**
//     * 获取系统运行时间
//     */
//    private Duration getUptime() {
//        // 这里可以从系统启动时间计算
//        return Duration.ofSeconds(System.currentTimeMillis() / 1000);
//    }
//
//    /**
//     * 获取活跃连接数
//     */
//    private int getActiveConnectionCount() {
//        // Spanner客户端内部管理连接，这里返回估计值
//        return spannerConfig.getMinConnections();
//    }
//
//    /**
//     * 获取待处理请求数
//     */
//    private long getPendingRequestCount() {
//        return executorService == null ? 0 :
//            ((ThreadPoolExecutor) executorService).getTaskCount();
//    }
//}