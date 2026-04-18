package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaWriteApiService;
import org.kitona.zus.api.request.FgaAuditMetadataRequest;
import org.kitona.zus.api.request.FgaTupleMutationRequest;
import org.kitona.zus.api.request.FgaTupleRequest;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA 元组写入 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaWriteApiService implements IFgaWriteApiService {

    @Resource
    private ITupleMutationApplicationService writeApplicationService;

    @Override
    public RestResult<Void> write(String storeId, FgaTupleMutationRequest request) {
        List<WriteTupleCommand> writeTuple = toCommands(request);
        writeApplicationService.write(storeId, writeTuple);
        return RestResult.success(null);
    }

    @Override
    public RestResult<Void> delete(String storeId, FgaTupleMutationRequest request) {
        List<WriteTupleCommand> writeTuple = toCommands(request);
        writeApplicationService.delete(storeId, writeTuple);
        return RestResult.success(null);
    }

    private List<WriteTupleCommand> toCommands(FgaTupleMutationRequest request) {
        if (request == null || request.getTuples() == null) {
            return List.of();
        }
        return request.getTuples().stream()
                .map(tuple -> toCommand(tuple, request.getAudit()))
                .toList();
    }

    private WriteTupleCommand toCommand(FgaTupleRequest tuple, FgaAuditMetadataRequest audit) {
        WriteTupleCommand command = WriteTupleCommand.builder()
                .objectType(tuple.getObject().getType())
                .objectId(tuple.getObject().getId())
                .relation(tuple.getRelation())
                .subjectType(tuple.getSubject().getType())
                .subjectId(tuple.getSubject().getId())
                .subjectRelation(tuple.getSubject().getRelation())
                .conditionDefinitionId(tuple.getCondition() == null ? null : tuple.getCondition().getConditionDefinitionId())
                .conditionContext(tuple.getCondition() == null || tuple.getCondition().getContext() == null
                        ? null
                        : JacksonUtil.toJSONString(tuple.getCondition().getContext()))
                .expiresAt(tuple.getExpiresAt())
                .build();
        if (audit != null) {
            command.setAuditMetadata(WriteTupleCommand.AuditMetadataInput.builder()
                    .operatorId(audit.getOperatorId())
                    .requestId(audit.getRequestId())
                    .source(audit.getSource())
                    .build());
        }
        return command;
    }
}
