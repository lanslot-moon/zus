//package org.kitona.zus.service.domain.leopard.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.kitona.zus.storage.zanzibar.leopard.*;
//import org.kitona.zus.storage.zanzibar.leopard.LeopardIndexService.*;
//import org.kitona.zus.storage.zanzibar.changelog.ChangeLogService;
//import org.kitona.zus.storage.zanzibar.tuple.TupleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.time.*;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.stream.Collectors;
//
///**
// * Leopard索引系统实现
// * 专门优化大型嵌套集合的计算
// * 通过Watch API监听ACL数据变更
// */
//@Slf4j
//@Service
//public class LeopardIndexServiceImpl implements LeopardIndexService {
//
//    @Autowired
//    private LeopardIndexConfig config;
//
//    @Autowired
//    private ChangeLogService changeLogService;
//
//    @Autowired
//    private TupleService tupleService;
//
//    // 索引存储
//    private final Map<String, NamespaceIndex> namespaceIndexes = new ConcurrentHashMap<>();
//    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
//
//    // 监听器
//    private final List<IndexChangeListener> listeners = new CopyOnWriteArrayList<>();
//
//    // 系统状态
//    private final AtomicBoolean systemHealthy = new AtomicBoolean(false);
//    private final AtomicLong lastHealthCheck = new AtomicLong(0);
//
//    // 统计信息
//    private final Map<String, QueryStats> queryStats = new ConcurrentHashMap<>();
//    private final Map<String, CacheStats> cacheStats = new ConcurrentHashMap<>();
//
//    // 线程池和调度器
//    private ExecutorService queryExecutor;
//    private ExecutorService indexUpdateExecutor;
//    private ScheduledExecutorService maintenanceScheduler;
//    private ScheduledExecutorService statsCollector;
//
//    // 变更日志监听
//    private volatile boolean watchingChangeLog = false;
//
//    @PostConstruct
//    public void initialize() {
//        log.info("正在初始化Leopard索引系统...");
//
//        try {
//            // 初始化线程池
//            queryExecutor = Executors.newFixedThreadPool(config.getMaxQueryConcurrency());
//            indexUpdateExecutor = Executors.newFixedThreadPool(config.getIndexConcurrency());
//            maintenanceScheduler = Executors.newSingleThreadScheduledExecutor();
//            statsCollector = Executors.newSingleThreadScheduledExecutor();
//
//            // 启动维护任务
//            startMaintenanceTasks();
//
//            // 启动统计信息收集
//            startStatsCollection();
//
//            systemHealthy.set(true);
//            log.info("Leopard索引系统初始化完成");
//
//        } catch (Exception e) {
//            log.error("Leopard索引系统初始化失败", e);
//            throw new RuntimeException("Leopard索引系统初始化失败", e);
//        }
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        log.info("正在关闭Leopard索引系统...");
//
//        // 停止变更监听
//        stopWatchingChangeLog();
//
//        // 关闭线程池
//        shutdownExecutor(queryExecutor);
//        shutdownExecutor(indexUpdateExecutor);
//        shutdownExecutor(maintenanceScheduler);
//        shutdownExecutor(statsCollector);
//
//        systemHealthy.set(false);
//        log.info("Leopard索引系统已关闭");
//    }
//
//    @Override
//    public void initialize() {
//        // 这个方法在@PostConstruct中已经执行了
//        log.info("Leopard索引系统已初始化");
//    }
//
//    @Override
//    public void shutdown() {
//        // 这个方法在@PreDestroy中已经执行了
//        log.info("Leopard索引系统准备关闭");
//    }
//
//    @Override
//    public CompletableFuture<IndexStats> createNamespaceIndex(String namespace) {
//        log.info("正在为命名空间创建索引: {}", namespace);
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.writeLock().lock();
//
//                if (namespaceIndexes.containsKey(namespace)) {
//                    log.warn("命名空间索引已存在: {}", namespace);
//                    return getIndexStats(namespace);
//                }
//
//                // 创建命名空间索引
//                NamespaceIndex index = new NamespaceIndex(namespace, config);
//                namespaceIndexes.put(namespace, index);
//
//                // 异步加载数据
//                CompletableFuture.runAsync(() -> loadNamespaceData(index), indexUpdateExecutor);
//
//                // 通知监听器
//                IndexStats stats = index.getStats();
//                notifyListeners(listener -> listener.onIndexCreated(namespace, stats));
//
//                log.info("命名空间索引创建成功: {}", namespace);
//                return stats;
//
//            } finally {
//                rwLock.writeLock().unlock();
//            }
//        }, queryExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Boolean> deleteNamespaceIndex(String namespace) {
//        log.info("正在删除命名空间索引: {}", namespace);
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.writeLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.remove(namespace);
//                if (index != null) {
//                    index.shutdown();
//
//                    // 清理统计信息
//                    queryStats.remove(namespace);
//                    cacheStats.remove(namespace);
//
//                    // 通知监听器
//                    notifyListeners(listener -> listener.onIndexDeleted(namespace));
//
//                    log.info("命名空间索引删除成功: {}", namespace);
//                    return true;
//                }
//
//                log.warn("命名空间索引不存在: {}", namespace);
//                return false;
//
//            } finally {
//                rwLock.writeLock().unlock();
//            }
//        }, queryExecutor);
//    }
//
//    @Override
//    public CompletableFuture<ComputationResult> computeSet(QueryRequest request) {
//        log.debug("执行集合计算查询: namespace={}, objectId={}, relation={}",
//            request.getNamespace(), request.getObjectId(), request.getRelation());
//
//        return CompletableFuture.supplyAsync(() -> {
//            long startTime = System.currentTimeMillis();
//
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(request.getNamespace());
//                if (index == null) {
//                    return ComputationResult.builder()
//                        .queryId(generateQueryId())
//                        .result(Collections.emptySet())
//                        .computedAt(Instant.now())
//                        .computationTimeMs(System.currentTimeMillis() - startTime)
//                        .build();
//                }
//
//                // 检查缓存
//                if (request.isUseCache()) {
//                    ComputationResult cachedResult = index.getFromCache(request);
//                    if (cachedResult != null) {
//                        updateCacheStats(request.getNamespace(), true);
//                        return cachedResult;
//                    }
//                }
//
//                // 执行查询
//                Set<String> result = index.computeSet(request);
//
//                ComputationResult computationResult = ComputationResult.builder()
//                    .queryId(generateQueryId())
//                    .result(result)
//                    .computedAt(Instant.now())
//                    .computationTimeMs(System.currentTimeMillis() - startTime)
//                    .totalRecords(index.getRecordCount())
//                    .fromCache(false)
//                    .indexVersion(index.getVersion())
//                    .queryMetadata(createQueryMetadata(request))
//                    .build();
//
//                // 缓存结果
//                if (request.isUseCache()) {
//                    index.putToCache(request, computationResult);
//                }
//
//                // 更新统计信息
//                updateQueryStats(request.getNamespace(), System.currentTimeMillis() - startTime, true);
//                updateCacheStats(request.getNamespace(), false);
//
//                return computationResult;
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, queryExecutor);
//    }
//
//    @Override
//    public CompletableFuture<List<ComputationResult>> computeBatchSets(List<QueryRequest> requests) {
//        log.debug("执行批量集合计算查询: {} 个请求", requests.size());
//
//        // 将请求按命名空间分组
//        Map<String, List<QueryRequest>> groupedRequests = requests.stream()
//            .collect(Collectors.groupingBy(QueryRequest::getNamespace));
//
//        List<CompletableFuture<ComputationResult>> futures = new ArrayList<>();
//
//        for (Map.Entry<String, List<QueryRequest>> entry : groupedRequests.entrySet()) {
//            String namespace = entry.getKey();
//            List<QueryRequest> namespaceRequests = entry.getValue();
//
//            // 并行处理每个命名空间的请求
//            for (QueryRequest request : namespaceRequests) {
//                futures.add(computeSet(request));
//            }
//        }
//
//        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//            .thenApply(v -> futures.stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList()));
//    }
//
//    @Override
//    public CompletableFuture<Boolean> updateIndex(String namespace, IndexRecord record) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index == null) {
//                    log.warn("命名空间索引不存在: {}", namespace);
//                    return false;
//                }
//
//                // 更新索引
//                index.updateRecord(record);
//
//                // 通知监听器
//                IndexUpdateEvent event = IndexUpdateEvent.builder()
//                    .namespace(namespace)
//                    .operation("UPDATE")
//                    .affectedRecords(Collections.singletonList(record))
//                    .timestamp(Instant.now())
//                    .source("WATCH_API")
//                    .build();
//
//                notifyListeners(listener -> listener.onIndexUpdate(event));
//
//                return true;
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Boolean> batchUpdateIndex(String namespace, List<IndexRecord> records) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index == null) {
//                    log.warn("命名空间索引不存在: {}", namespace);
//                    return false;
//                }
//
//                // 批量更新索引
//                index.batchUpdateRecords(records);
//
//                // 通知监听器
//                IndexUpdateEvent event = IndexUpdateEvent.builder()
//                    .namespace(namespace)
//                    .operation("BATCH_UPDATE")
//                    .affectedRecords(records)
//                    .timestamp(Instant.now())
//                    .source("WATCH_API")
//                    .build();
//
//                notifyListeners(listener -> listener.onIndexUpdate(event));
//
//                return true;
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public CompletableFuture<IndexStats> rebuildIndexFromChangeLog(String namespace, Instant fromTimestamp) {
//        log.info("从变更日志重建索引: namespace={}, fromTimestamp={}", namespace, fromTimestamp);
//
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.writeLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index == null) {
//                    log.warn("命名空间索引不存在: {}", namespace);
//                    return null;
//                }
//
//                // 从变更日志获取变更记录
//                ChangeLogService.ChangeLogQuery query = ChangeLogService.ChangeLogQuery.builder()
//                    .namespace(namespace)
//                    .fromTimestamp(fromTimestamp)
//                    .build();
//
//                List<ChangeLogService.ChangeLogRecord> changes = changeLogService.getChangeLogs(query);
//
//                // 清空现有索引
//                index.clear();
//
//                // 重建索引
//                for (ChangeLogService.ChangeLogRecord change : changes) {
//                    IndexRecord record = IndexRecord.builder()
//                        .namespace(change.getNamespace())
//                        .objectId(change.getObjectId())
//                        .relation(change.getRelation())
//                        .subjects(change.getSubjects())
//                        .lastModified(change.getTimestamp())
//                        .version(change.getVersion())
//                        .build();
//
//                    index.updateRecord(record);
//                }
//
//                // 优化索引
//                index.optimize();
//
//                // 通知监听器
//                IndexUpdateEvent event = IndexUpdateEvent.builder()
//                    .namespace(namespace)
//                    .operation("REBUILD")
//                    .affectedRecords(index.getAllRecords())
//                    .timestamp(Instant.now())
//                    .source("CHANGE_LOG")
//                    .build();
//
//                notifyListeners(listener -> listener.onIndexUpdate(event));
//
//                log.info("索引重建完成: namespace={}, records={}", namespace, index.getRecordCount());
//                return index.getStats();
//
//            } catch (Exception e) {
//                log.error("重建索引失败: namespace={}", namespace, e);
//                throw new RuntimeException("重建索引失败", e);
//            } finally {
//                rwLock.writeLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public IndexStats getIndexStats(String namespace) {
//        try {
//            rwLock.readLock().lock();
//
//            NamespaceIndex index = namespaceIndexes.get(namespace);
//            return index != null ? index.getStats() : IndexStats.builder().build();
//
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public Map<String, IndexStats> getAllIndexStats() {
//        try {
//            rwLock.readLock().lock();
//
//            return namespaceIndexes.entrySet().stream()
//                .collect(Collectors.toMap(
//                    Map.Entry::getKey,
//                    entry -> entry.getValue().getStats()
//                ));
//
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public boolean indexExists(String namespace) {
//        try {
//            rwLock.readLock().lock();
//            return namespaceIndexes.containsKey(namespace);
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public String getHealthStatus(String namespace) {
//        try {
//            rwLock.readLock().lock();
//
//            NamespaceIndex index = namespaceIndexes.get(namespace);
//            return index != null ? index.getHealthStatus() : "INDEX_NOT_FOUND";
//
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public CompletableFuture<Void> optimizeIndex(String namespace) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index != null) {
//                    index.optimize();
//
//                    // 通知监听器
//                    notifyListeners(listener -> listener.onIndexOptimized(namespace));
//                }
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Integer> cleanupExpiredIndexes() {
//        return CompletableFuture.supplyAsync(() -> {
//            int cleanedCount = 0;
//
//            try {
//                rwLock.writeLock().lock();
//
//                Instant expireTime = Instant.now().minus(Duration.ofHours(24));
//
//                Iterator<Map.Entry<String, NamespaceIndex>> iterator = namespaceIndexes.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry<String, NamespaceIndex> entry = iterator.next();
//                    NamespaceIndex index = entry.getValue();
//
//                    if (index.getLastAccessTime().isBefore(expireTime)) {
//                        iterator.remove();
//                        index.shutdown();
//                        cleanedCount++;
//
//                        // 通知监听器
//                        notifyListeners(listener -> listener.onIndexDeleted(entry.getKey()));
//                    }
//                }
//
//            } finally {
//                rwLock.writeLock().unlock();
//            }
//
//            log.info("清理过期索引完成: {} 个索引", cleanedCount);
//            return cleanedCount;
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public List<ComputationResult> getQueryHistory(String namespace, Instant fromTime, Instant toTime) {
//        QueryStats stats = queryStats.get(namespace);
//        return stats != null ? stats.getQueryHistory(fromTime, toTime) : Collections.emptyList();
//    }
//
//    @Override
//    public void clearCache(String namespace) {
//        try {
//            rwLock.readLock().lock();
//
//            NamespaceIndex index = namespaceIndexes.get(namespace);
//            if (index != null) {
//                index.clearCache();
//                cacheStats.remove(namespace);
//            }
//
//        } finally {
//            rwLock.readLock().unlock();
//        }
//    }
//
//    @Override
//    public CompletableFuture<Void> warmupCache(String namespace) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index != null) {
//                    index.warmupCache();
//                }
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public CompletableFuture<String> exportIndex(String namespace, String format) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.readLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index == null) {
//                    throw new IllegalArgumentException("命名空间索引不存在: " + namespace);
//                }
//
//                return index.exportData(format);
//
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }, queryExecutor);
//    }
//
//    @Override
//    public CompletableFuture<Void> importIndex(String namespace, String data, String format) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                rwLock.writeLock().lock();
//
//                NamespaceIndex index = namespaceIndexes.get(namespace);
//                if (index == null) {
//                    throw new IllegalArgumentException("命名空间索引不存在: " + namespace);
//                }
//
//                index.importData(data, format);
//
//            } finally {
//                rwLock.writeLock().unlock();
//            }
//        }, indexUpdateExecutor);
//    }
//
//    @Override
//    public SystemStatus getSystemStatus() {
//        long currentTime = System.currentTimeMillis();
//        lastHealthCheck.set(currentTime);
//
//        return SystemStatus.builder()
//            .healthy(systemHealthy.get())
//            .lastHealthCheck(Instant.ofEpochMilli(currentTime))
//            .totalNamespaces(namespaceIndexes.size())
//            .totalRecords(namespaceIndexes.values().stream()
//                .mapToLong(NamespaceIndex::getRecordCount)
//                .sum())
//            .totalMemoryUsageBytes(calculateMemoryUsage())
//            .activeQueries(((ThreadPoolExecutor) queryExecutor).getActiveCount())
//            .version(config.getIndexVersion())
//            .warnings(collectWarnings())
//            .errors(collectErrors())
//            .performanceMetrics(collectPerformanceMetrics())
//            .build();
//    }
//
//    @Override
//    public void registerChangeListener(IndexChangeListener listener) {
//        listeners.add(listener);
//    }
//
//    @Override
//    public void unregisterChangeListener(IndexChangeListener listener) {
//        listeners.remove(listener);
//    }
//
//    @Override
//    public void startWatchingChangeLog() {
//        if (watchingChangeLog) {
//            log.warn("变更日志监听已经在运行");
//            return;
//        }
//
//        log.info("开始监听变更日志...");
//        watchingChangeLog = true;
//
//        // 启动变更日志监听任务
//        maintenanceScheduler.scheduleAtFixedRate(this::processChangeLogUpdates,
//            0, config.getUpdateIntervalMinutes(), TimeUnit.MINUTES);
//    }
//
//    @Override
//    public void stopWatchingChangeLog() {
//        if (!watchingChangeLog) {
//            return;
//        }
//
//        log.info("停止监听变更日志...");
//        watchingChangeLog = false;
//    }
//
//    // 私有方法
//
//    private void shutdownExecutor(ExecutorService executor) {
//        if (executor != null && !executor.isShutdown()) {
//            executor.shutdown();
//            try {
//                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
//                    executor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executor.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    private void startMaintenanceTasks() {
//        // 定时清理过期缓存
//        maintenanceScheduler.scheduleAtFixedRate(() -> {
//            namespaceIndexes.values().forEach(NamespaceIndex::cleanupCache);
//        }, 1, 1, TimeUnit.HOURS);
//
//        // 定时优化索引
//        maintenanceScheduler.scheduleAtFixedRate(() -> {
//            namespaceIndexes.values().forEach(NamespaceIndex::optimize);
//        }, 6, 6, TimeUnit.HOURS);
//    }
//
//    private void startStatsCollection() {
//        statsCollector.scheduleAtFixedRate(this::collectStats,
//            0, config.getStatsCollectionIntervalSeconds(), TimeUnit.SECONDS);
//    }
//
//    private void processChangeLogUpdates() {
//        if (!watchingChangeLog) {
//            return;
//        }
//
//        try {
//            Instant lastProcessedTime = getLastProcessedChangeLogTime();
//            Instant now = Instant.now();
//
//            // 获取变更记录
//            ChangeLogService.ChangeLogQuery query = ChangeLogService.ChangeLogQuery.builder()
//                .fromTimestamp(lastProcessedTime)
//                .toTimestamp(now)
//                .build();
//
//            List<ChangeLogService.ChangeLogRecord> changes = changeLogService.getChangeLogs(query);
//
//            // 按命名空间分组并处理
//            changes.stream()
//                .collect(Collectors.groupingBy(ChangeLogService.ChangeLogRecord::getNamespace))
//                .forEach((namespace, namespaceChanges) -> {
//                    List<IndexRecord> indexRecords = namespaceChanges.stream()
//                        .map(change -> IndexRecord.builder()
//                            .namespace(change.getNamespace())
//                            .objectId(change.getObjectId())
//                            .relation(change.getRelation())
//                            .subjects(change.getSubjects())
//                            .lastModified(change.getTimestamp())
//                            .version(change.getVersion())
//                            .build())
//                        .collect(Collectors.toList());
//
//                    batchUpdateIndex(namespace, indexRecords);
//                });
//
//        } catch (Exception e) {
//            log.error("处理变更日志更新失败", e);
//        }
//    }
//
//    private void loadNamespaceData(NamespaceIndex index) {
//        try {
//            log.info("正在加载命名空间数据: {}", index.getNamespace());
//
//            // 从元组服务获取数据
//            // 这里应该调用具体的TupleService方法
//            // List<TupleService.RelationshipTuple> tuples = tupleService.getAllTuples(index.getNamespace());
//
//            // 模拟数据加载
//            for (int i = 0; i < 1000; i++) {
//                IndexRecord record = IndexRecord.builder()
//                    .namespace(index.getNamespace())
//                    .objectId("object_" + i)
//                    .relation("read")
//                    .subjects(Set.of("user_" + i))
//                    .lastModified(Instant.now())
//                    .version(1L)
//                    .build();
//
//                index.updateRecord(record);
//            }
//
//            log.info("命名空间数据加载完成: {}, records={}", index.getNamespace(), index.getRecordCount());
//
//        } catch (Exception e) {
//            log.error("加载命名空间数据失败: {}", index.getNamespace(), e);
//        }
//    }
//
//    private void notifyListeners(java.util.function.Consumer<IndexChangeListener> action) {
//        for (IndexChangeListener listener : listeners) {
//            try {
//                action.accept(listener);
//            } catch (Exception e) {
//                log.error("通知监听器失败", e);
//            }
//        }
//    }
//
//    private String generateQueryId() {
//        return "query_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
//    }
//
//    private Map<String, Object> createQueryMetadata(QueryRequest request) {
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put("namespace", request.getNamespace());
//        metadata.put("objectId", request.getObjectId());
//        metadata.put("relation", request.getRelation());
//        metadata.put("useCache", request.isUseCache());
//        return metadata;
//    }
//
//    private void updateQueryStats(String namespace, long queryTimeMs, boolean success) {
//        QueryStats stats = queryStats.computeIfAbsent(namespace, k -> new QueryStats());
//        stats.recordQuery(queryTimeMs, success);
//    }
//
//    private void updateCacheStats(String namespace, boolean hit) {
//        CacheStats stats = cacheStats.computeIfAbsent(namespace, k -> new CacheStats());
//        if (hit) {
//            stats.recordHit();
//        } else {
//            stats.recordMiss();
//        }
//    }
//
//    private Instant getLastProcessedChangeLogTime() {
//        // 这里应该从持久化存储中获取最后一次处理的时间
//        // 简化实现，返回1小时前
//        return Instant.now().minus(Duration.ofHours(1));
//    }
//
//    private long calculateMemoryUsage() {
//        // 简化实现，返回估计的内存使用量
//        return namespaceIndexes.values().stream()
//            .mapToLong(NamespaceIndex::estimateMemoryUsage)
//            .sum();
//    }
//
//    private List<String> collectWarnings() {
//        List<String> warnings = new ArrayList<>();
//
//        // 检查内存使用
//        long memoryUsage = calculateMemoryUsage();
//        if (memoryUsage > config.getMaxIndexSizeMB() * 1024 * 1024L) {
//            warnings.add("内存使用量超过限制");
//        }
//
//        // 检查索引健康状态
//        for (Map.Entry<String, NamespaceIndex> entry : namespaceIndexes.entrySet()) {
//            if (!entry.getValue().isHealthy()) {
//                warnings.add("索引不健康: " + entry.getKey());
//            }
//        }
//
//        return warnings;
//    }
//
//    private List<String> collectErrors() {
//        List<String> errors = new ArrayList<>();
//
//        // 检查系统健康状态
//        if (!systemHealthy.get()) {
//            errors.add("系统不健康");
//        }
//
//        return errors;
//    }
//
//    private Map<String, Object> collectPerformanceMetrics() {
//        Map<String, Object> metrics = new HashMap<>();
//
//        // 平均查询时间
//        double avgQueryTime = queryStats.values().stream()
//            .mapToDouble(QueryStats::getAverageQueryTime)
//            .average()
//            .orElse(0.0);
//        metrics.put("averageQueryTimeMs", avgQueryTime);
//
//        // 缓存命中率
//        double hitRate = cacheStats.values().stream()
//            .mapToDouble(CacheStats::getHitRate)
//            .average()
//            .orElse(0.0);
//        metrics.put("cacheHitRate", hitRate);
//
//        // 活跃查询数
//        metrics.put("activeQueries", ((ThreadPoolExecutor) queryExecutor).getActiveCount());
//
//        return metrics;
//    }
//
//    private void collectStats() {
//        // 这个方法已经集成在getSystemStatus中了
//        // 这里可以进行更详细的统计信息收集
//    }
//
//    // 内部类
//
//    /**
//     * 命名空间索引
//     */
//    private static class NamespaceIndex {
//        private final String namespace;
//        private final LeopardIndexConfig config;
//        private final Map<String, IndexRecord> records = new ConcurrentHashMap<>();
//        private final Map<String, ComputationResult> cache = new ConcurrentHashMap<>();
//        private final Instant createdTime;
//        private volatile Instant lastAccessTime;
//
//        public NamespaceIndex(String namespace, LeopardIndexConfig config) {
//            this.namespace = namespace;
//            this.config = config;
//            this.createdTime = Instant.now();
//            this.lastAccessTime = Instant.now();
//        }
//
//        public String getNamespace() {
//            return namespace;
//        }
//
//        public long getRecordCount() {
//            return records.size();
//        }
//
//        public Instant getLastAccessTime() {
//            return lastAccessTime;
//        }
//
//        public String getVersion() {
//            return config.getIndexVersion();
//        }
//
//        public void updateRecord(IndexRecord record) {
//            lastAccessTime = Instant.now();
//            records.put(generateRecordKey(record), record);
//        }
//
//        public void batchUpdateRecords(List<IndexRecord> newRecords) {
//            lastAccessTime = Instant.now();
//            for (IndexRecord record : newRecords) {
//                records.put(generateRecordKey(record), record);
//            }
//        }
//
//        public void clear() {
//            records.clear();
//            cache.clear();
//        }
//
//        public void optimize() {
//            // 索引优化逻辑
//            // 这里可以实现压缩、重平衡等优化操作
//            lastAccessTime = Instant.now();
//        }
//
//        public void cleanupCache() {
//            // 清理过期缓存
//            Instant expireTime = Instant.now().minus(Duration.ofMinutes(30));
//            cache.entrySet().removeIf(entry ->
//                entry.getValue().getComputedAt().isBefore(expireTime));
//        }
//
//        public void warmupCache() {
//            // 预热缓存逻辑
//            // 这里可以加载常用的查询结果到缓存中
//        }
//
//        public void clearCache() {
//            cache.clear();
//        }
//
//        public ComputationResult getFromCache(QueryRequest request) {
//            String cacheKey = generateCacheKey(request);
//            ComputationResult result = cache.get(cacheKey);
//
//            if (result != null &&
//                result.getComputedAt().isAfter(Instant.now().minus(Duration.ofMinutes(5)))) {
//                return result;
//            }
//
//            return null;
//        }
//
//        public void putToCache(QueryRequest request, ComputationResult result) {
//            if (cache.size() >= config.getCacheSize()) {
//                // 缓存满了，移除最老的条目
//                String oldestKey = cache.entrySet().stream()
//                    .min(Map.Entry.comparingByValue(
//                        Comparator.comparing(ComputationResult::getComputedAt)))
//                    .map(Map.Entry::getKey)
//                    .orElse(null);
//
//                if (oldestKey != null) {
//                    cache.remove(oldestKey);
//                }
//            }
//
//            String cacheKey = generateCacheKey(request);
//            cache.put(cacheKey, result);
//        }
//
//        public Set<String> computeSet(QueryRequest request) {
//            lastAccessTime = Instant.now();
//
//            return records.values().stream()
//                .filter(record ->
//                    record.getObjectId().equals(request.getObjectId()) &&
//                    record.getRelation().equals(request.getRelation()))
//                .flatMap(record -> record.getSubjects().stream())
//                .collect(Collectors.toSet());
//        }
//
//        public boolean isHealthy() {
//            return records.size() >= 0; // 简化实现
//        }
//
//        public String getHealthStatus() {
//            return isHealthy() ? "HEALTHY" : "UNHEALTHY";
//        }
//
//        public IndexStats getStats() {
//            return IndexStats.builder()
//                .namespace(namespace)
//                .totalRecords(records.size())
//                .indexSizeBytes(estimateMemoryUsage())
//                .lastUpdateTime(lastAccessTime)
//                .queryCount(0) // 这里应该从实际统计中获取
//                .averageQueryTimeMs(0.0) // 这里应该从实际统计中获取
//                .cacheHitCount(0) // 这里应该从实际统计中获取
//                .cacheMissCount(0) // 这里应该从实际统计中获取
//                .healthStatus(getHealthStatus())
//                .shardCount(1) // 简化实现
//                .build();
//        }
//
//        public String exportData(String format) {
//            // 导出索引数据
//            switch (format.toUpperCase()) {
//                case "JSON":
//                    return "{\"namespace\":\"" + namespace + "\",\"records\":" + records.size() + "}";
//                case "XML":
//                    return "<namespace><name>" + namespace + "</name><records>" + records.size() + "</records></namespace>";
//                default:
//                    return namespace + "," + records.size();
//            }
//        }
//
//        public void importData(String data, String format) {
//            // 导入索引数据
//            // 这里应该解析数据并更新索引
//            clear();
//        }
//
//        public long estimateMemoryUsage() {
//            // 简化实现，返回估计的内存使用量
//            return records.size() * 1024L; // 每个记录假设1KB
//        }
//
//        public void shutdown() {
//            records.clear();
//            cache.clear();
//        }
//
//        public List<IndexRecord> getAllRecords() {
//            return new ArrayList<>(records.values());
//        }
//
//        private String generateRecordKey(IndexRecord record) {
//            return record.getObjectId() + "|" + record.getRelation();
//        }
//
//        private String generateCacheKey(QueryRequest request) {
//            return request.getObjectId() + "|" + request.getRelation() +
//                   "|" + request.getSubjectFilters();
//        }
//    }
//
//    /**
//     * 查询统计信息
//     */
//    private static class QueryStats {
//        private final List<QueryRecord> history = new CopyOnWriteArrayList<>();
//        private final AtomicLong totalQueries = new AtomicLong(0);
//        private final AtomicLong totalTime = new AtomicLong(0);
//        private final AtomicLong successfulQueries = new AtomicLong(0);
//
//        public void recordQuery(long queryTimeMs, boolean success) {
//            QueryRecord record = new QueryRecord(Instant.now(), queryTimeMs, success);
//            history.add(record);
//
//            totalQueries.incrementAndGet();
//            totalTime.addAndGet(queryTimeMs);
//            if (success) {
//                successfulQueries.incrementAndGet();
//            }
//
//            // 保持历史记录在合理范围内
//            if (history.size() > 10000) {
//                history.subList(0, 1000).clear();
//            }
//        }
//
//        public double getAverageQueryTime() {
//            long total = totalQueries.get();
//            return total > 0 ? (double) totalTime.get() / total : 0.0;
//        }
//
//        public List<ComputationResult> getQueryHistory(Instant fromTime, Instant toTime) {
//            return history.stream()
//                .filter(record ->
//                    record.getTimestamp().isAfter(fromTime) &&
//                    record.getTimestamp().isBefore(toTime))
//                .map(record -> ComputationResult.builder()
//                    .queryId("hist_" + record.getTimestamp().toEpochMilli())
//                    .result(Collections.emptySet())
//                    .computedAt(record.getTimestamp())
//                    .computationTimeMs(record.getQueryTimeMs())
//                    .build())
//                .collect(Collectors.toList());
//        }
//
//        private static class QueryRecord {
//            private final Instant timestamp;
//            private final long queryTimeMs;
//            private final boolean success;
//
//            public QueryRecord(Instant timestamp, long queryTimeMs, boolean success) {
//                this.timestamp = timestamp;
//                this.queryTimeMs = queryTimeMs;
//                this.success = success;
//            }
//
//            public Instant getTimestamp() {
//                return timestamp;
//            }
//
//            public long getQueryTimeMs() {
//                return queryTimeMs;
//            }
//        }
//    }
//
//    /**
//     * 缓存统计信息
//     */
//    private static class CacheStats {
//        private final AtomicLong hits = new AtomicLong(0);
//        private final AtomicLong misses = new AtomicLong(0);
//
//        public void recordHit() {
//            hits.incrementAndGet();
//        }
//
//        public void recordMiss() {
//            misses.incrementAndGet();
//        }
//
//        public double getHitRate() {
//            long total = hits.get() + misses.get();
//            return total > 0 ? (double) hits.get() / total : 0.0;
//        }
//
//        public long getHits() {
//            return hits.get();
//        }
//
//        public long getMisses() {
//            return misses.get();
//        }
//    }
//}