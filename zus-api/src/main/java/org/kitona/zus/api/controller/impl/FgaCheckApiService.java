package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.controller.IFgaCheckApiService;
import org.kitona.zus.api.converter.FgaCheckConverter;
import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.response.FgaBatchCheckResultVO;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @Resource
    private IPermissionCheckApplicationService permissionCheckApplicationService;

    @Override
    public RestResult<FgaCheckResultVO> check(String storeId, FgaCheckRequest request) {
        long startNanos = System.nanoTime();
        CheckCommand command = FgaCheckConverter.toCheckCommand(storeId, request);
        PermissionCheckResultDTO dto = permissionCheckApplicationService.check(command);
        if (log.isDebugEnabled()) {
            long costMs = (System.nanoTime() - startNanos) / 1_000_000L;
            log.debug("FgaCheckApiService.check storeId={}, cost={}ms", storeId, costMs);
        }
        return RestResult.success(FgaCheckConverter.toVO(dto));
    }

    @Override
    public RestResult<FgaBatchCheckResultVO> batchCheck(String storeId, FgaBatchCheckRequest request) {
        long startNanos = System.nanoTime();
        List<FgaBatchCheckRequest.CheckItem> items = request != null ? request.getChecks() : null;
        if (CollectionUtils.isEmpty(items)) {
            return RestResult.success(FgaBatchCheckResultVO.builder()
                    .results(Map.of())
                    .durationMs(0L)
                    .zookie(null)
                    .build());
        }

        List<CompletableFuture<Map.Entry<String, FgaCheckResultVO>>> futures = items.stream()
                .map(item -> {
                    CheckCommand command = FgaCheckConverter.toCheckCommand(storeId, item, request);
                    return permissionCheckApplicationService.checkAsync(command)
                            .thenApply(dto -> Map.entry(item.getCorrelationId(), FgaCheckConverter.toVO(dto)));
                })
                .toList();

        Map<String, FgaCheckResultVO> results = new HashMap<>(items.size() * 2);
        String sharedZookie = null;
        for (CompletableFuture<Map.Entry<String, FgaCheckResultVO>> future : futures) {
            Map.Entry<String, FgaCheckResultVO> entry = future.join();
            results.put(entry.getKey(), entry.getValue());
            if (sharedZookie == null && entry.getValue() != null) {
                sharedZookie = entry.getValue().getZookieToken();
            }
        }

        long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
        log.info("FgaCheckApiService.batchCheck storeId={}, count={}, cost={}ms",
                storeId, items.size(), durationMs);

        return RestResult.success(FgaBatchCheckResultVO.builder()
                .results(results)
                .durationMs(durationMs)
                .zookie(sharedZookie)
                .build());
    }
}
