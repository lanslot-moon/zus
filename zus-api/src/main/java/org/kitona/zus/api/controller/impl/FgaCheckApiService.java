package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaCheckApiService;
import org.kitona.zus.api.request.FgaCheckRequest;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.springframework.stereotype.Service;

/**
 * FGA 权限检查 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaCheckApiService implements IFgaCheckApiService {

    @Resource
    private IPermissionCheckApplicationService checkApplicationService;

    @Override
    public RestResult<FgaCheckResultVO> check(String storeId, FgaCheckRequest request) {
        log.debug("FgaCheckApiService check, storeId:{}, params:{}", storeId, JacksonUtil.toJSONString(request));
        CheckCommand command = CheckCommand.builder()
                .storeId(storeId)
                .objectType(request.getObject().getType())
                .objectId(request.getObject().getId())
                .relation(request.getRelation())
                .subjectType(request.getSubject().getType())
                .subjectId(request.getSubject().getId())
                .subjectRelation(request.getSubject().getRelation())
                .consistencyToken(request.getConsistencyToken())
                .context(request.getContext())
                .build();
        PermissionCheckResultDTO dto = checkApplicationService.check(command);
        FgaCheckResultVO vo = FgaCheckResultVO.builder()
                .allowed(dto.isAllowed())
                .durationMs(dto.getDurationMs())
                .zookieToken(dto.getZookieToken())
                .decision(dto.getDecision())
                .errorMessage(dto.getErrorMessage())
                .build();
        return RestResult.success(vo);
    }
}
