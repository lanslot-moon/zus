package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.service.TupleMutationDomainService;
import org.kitona.zus.domain.service.TupleMutationResult;
import org.kitona.zus.domain.valueobject.TupleCondition;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.TupleMutationRequest;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * tuple 写删编排器
 *
 * <p>应用层仅负责将命令对象转换为领域可理解的变更请求，
 * 核心业务规则由 {@link TupleMutationDomainService} 统一处理。
 */
@Component
public class TupleWriteOrchestrator {

    @Resource
    private TupleMutationDomainService tupleMutationDomainService;

    public TupleWriteResult write(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleWriteResult.empty();
        }
        TupleMutationResult result = tupleMutationDomainService.write(storeId, toRequests(commands));
        return new TupleWriteResult(result.zookie(), result.writtenKeys(), result.deletedKeys());
    }

    public TupleWriteResult delete(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleWriteResult.empty();
        }
        TupleMutationResult result = tupleMutationDomainService.delete(storeId, toRequests(commands));
        return new TupleWriteResult(result.zookie(), result.writtenKeys(), result.deletedKeys());
    }

    private List<TupleMutationRequest> toRequests(List<WriteTupleCommand> commands) {
        return commands.stream()
                .map(command -> TupleMutationRequest.of(buildTupleKey(command),
                        TupleCondition.of(command.getConditionName(), command.getConditionContext())))
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
}
