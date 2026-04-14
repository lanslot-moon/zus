package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.port.IAuditContextProvider;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.service.TupleMutationDomainService;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * tuple 变更编排器
 *
 * <p>应用层仅负责将命令对象转换为领域可理解的变更请求，
 * 核心业务规则由 {@link TupleMutationDomainService} 统一处理。
 */
@Component
public class TupleMutationCoordinator {

    @Resource
    private TupleMutationDomainService tupleMutationDomainService;

    @Resource
    private IAuditContextProvider auditContextProvider;

    public TupleMutationOutcome write(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        org.kitona.zus.domain.service.TupleMutationOutcome result =
                tupleMutationDomainService.write(storeId, toRequests(commands));
        return new TupleMutationOutcome(result.zookie(), result.writtenKeys(), result.deletedKeys(), result.auditMetadata());
    }

    public TupleMutationOutcome delete(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        org.kitona.zus.domain.service.TupleMutationOutcome result =
                tupleMutationDomainService.delete(storeId, toRequests(commands));
        return new TupleMutationOutcome(result.zookie(), result.writtenKeys(), result.deletedKeys(), result.auditMetadata());
    }

    private List<TupleMutationRequest> toRequests(List<WriteTupleCommand> commands) {
        return commands.stream()
                .map(command -> TupleMutationRequest.of(
                        buildTupleKey(command),
                        TupleCondition.of(command.getConditionDefinitionId(),
                                command.getConditionName(),
                                command.getConditionContext()),
                        command.getExpiresAt(),
                        resolveAuditMetadata(command)))
                .toList();
    }

    private TupleKey buildTupleKey(WriteTupleCommand command) {
        return TupleKey.of(
                command.getObjectType(),
                command.getObjectId(),
                command.getRelation(),
                command.getSubjectType(),
                command.getSubjectId(),
                command.getSubjectRelation());
    }

    private AuditMetadata resolveAuditMetadata(WriteTupleCommand command) {
        WriteTupleCommand.AuditMetadataInput auditMetadata = command.getAuditMetadata();
        if (auditMetadata != null) {
            return AuditMetadata.of(auditMetadata.getOperatorId(), auditMetadata.getRequestId(), auditMetadata.getSource());
        }
        return auditContextProvider.current();
    }
}
