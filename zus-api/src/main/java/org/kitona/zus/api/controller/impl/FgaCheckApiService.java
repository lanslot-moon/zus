package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.controller.IFgaCheckApiService;
import org.kitona.zus.api.converter.FgaCheckConverter;
import org.kitona.zus.api.converter.FgaExplainConverter;
import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.response.FgaBatchCheckResultVO;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * FGA Check API 实现。
 *
 * <p>热路径实现重点：
 * <ul>
 *   <li>单次 {@code check} 采用同步调用，追求最低延迟；</li>
 *   <li>批量 {@code batchCheck} 基于应用服务的 {@code checkAsync} 并发执行，
 *       结果按 {@code correlationId} 聚合返回；</li>
 *   <li>批次级 zookie 取首条非空结果的 zookie（所有项共享 Store 的当前版本），
 *       若所有项都无 zookie 则为 null。</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@Slf4j
@Service
public class FgaCheckApiService implements IFgaCheckApiService {

    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    @Resource
    private IPermissionCheckApplicationService permissionCheckApplicationService;

    @Override
    public RestResult<FgaCheckResultVO> check(String storeId, FgaCheckRequest request) {
        long startNanos = System.nanoTime();
        CheckCommand command = FgaCheckConverter.INSTANCE.toCheckCommand(storeId, request);
        PermissionCheckResultDTO dto = permissionCheckApplicationService.check(command);
        if (log.isDebugEnabled()) {
            long costMs = elapsedMillis(startNanos);
            log.debug("FgaCheckApiService.check storeId={}, cost={}ms", storeId, costMs);
        }
        return RestResult.success(FgaCheckConverter.INSTANCE.toVO(dto));
    }

    @Override
    public RestResult<FgaExplainResultVO> explain(String storeId, FgaExplainRequest request) {
        long startNanos = System.nanoTime();
        ExplainCommand command = FgaExplainConverter.INSTANCE.toExplainCommand(storeId, request);
        PermissionExplainResultDTO dto = permissionCheckApplicationService.explain(command);
        long costMs = (System.nanoTime() - startNanos) / 1_000_000L;
        log.debug("FgaExplainApiService.explain storeId={}, cost={}ms", storeId, costMs);
        return RestResult.success(FgaExplainConverter.INSTANCE.toVO(dto));
    }

    @Override
    public RestResult<FgaBatchCheckResultVO> batchCheck(String storeId, FgaBatchCheckRequest request) {
        long startNanos = System.nanoTime();
        List<FgaBatchCheckRequest.CheckItem> items = checksOf(request);
        if (CollectionUtils.isEmpty(items)) {
            return RestResult.success(emptyBatchResult());
        }

        List<CompletableFuture<BatchCheckEntry>> tasks = createBatchTasks(storeId, request, items);
        List<BatchCheckEntry> entries = awaitBatchResults(tasks);
        long durationMs = elapsedMillis(startNanos);

        log.info("FgaCheckApiService.batchCheck storeId={}, count={}, cost={}ms",
                storeId, items.size(), durationMs);

        return RestResult.success(toBatchResult(entries, durationMs));
    }

    private List<FgaBatchCheckRequest.CheckItem> checksOf(FgaBatchCheckRequest request) {
        return request == null ? List.of() : request.getChecks();
    }

    private FgaBatchCheckResultVO emptyBatchResult() {
        return FgaBatchCheckResultVO.builder()
                .results(Map.of())
                .durationMs(0L)
                .zookie(null)
                .build();
    }

    private List<CompletableFuture<BatchCheckEntry>> createBatchTasks(String storeId, FgaBatchCheckRequest request,
                                                                      List<FgaBatchCheckRequest.CheckItem> items) {
        return items.stream()
                .map(item -> checkAsync(storeId, request, item))
                .toList();
    }

    private CompletableFuture<BatchCheckEntry> checkAsync(String storeId, FgaBatchCheckRequest request,
                                                          FgaBatchCheckRequest.CheckItem item) {
        CheckCommand command = FgaCheckConverter.INSTANCE.toCheckCommand(storeId, item, request);
        return permissionCheckApplicationService.checkAsync(command)
                .thenApply(dto -> new BatchCheckEntry(item.getCorrelationId(), FgaCheckConverter.INSTANCE.toVO(dto)));
    }

    private List<BatchCheckEntry> awaitBatchResults(List<CompletableFuture<BatchCheckEntry>> tasks) {
        return tasks.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private FgaBatchCheckResultVO toBatchResult(List<BatchCheckEntry> entries, long durationMs) {
        Map<String, FgaCheckResultVO> results = new LinkedHashMap<>(entries.size() * 2);
        String sharedZookie = null;

        for (BatchCheckEntry entry : entries) {
            FgaCheckResultVO result = entry.result();
            results.put(entry.correlationId(), result);
            if (sharedZookie == null && result != null) {
                sharedZookie = result.getZookieToken();
            }
        }

        return FgaBatchCheckResultVO.builder()
                .results(results)
                .durationMs(durationMs)
                .zookie(sharedZookie)
                .build();
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / NANOS_PER_MILLISECOND;
    }

    private record BatchCheckEntry(String correlationId, FgaCheckResultVO result) {
    }
}
