package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaExplainApiService;
import org.kitona.zus.api.converter.FgaExplainConverter;
import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IPermissionExplainApplicationService;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.springframework.stereotype.Service;

/**
 * FGA Explain API 实现。
 */
@Slf4j
@Service
public class FgaExplainApiService implements IFgaExplainApiService {

    @Resource
    private IPermissionExplainApplicationService permissionExplainApplicationService;

    @Override
    public RestResult<FgaExplainResultVO> explain(String storeId, FgaExplainRequest request) {
        long startNanos = System.nanoTime();
        ExplainCommand command = FgaExplainConverter.toExplainCommand(storeId, request);
        PermissionExplainResultDTO dto = permissionExplainApplicationService.explain(command);
        long costMs = (System.nanoTime() - startNanos) / 1_000_000L;
        log.debug("FgaExplainApiService.explain storeId={}, cost={}ms", storeId, costMs);
        return RestResult.success(FgaExplainConverter.toVO(dto));
    }
}
