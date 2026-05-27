package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
import org.kitona.zus.domain.port.IAuditContextProvider;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Tuple 变更请求装配器。
 *
 * <p>该组件只负责把应用层 {@link WriteTupleCommand} 转换为领域层
 * {@link TupleMutationRequest}，包括 tuple key、条件快照和审计元数据的装配。
 * 写入流程、zookie 分配、仓储保存和 changelog 持久化仍由 coordinator 编排。
 */
@Component
public class TupleMutationRequestAssembler {

    /**
     * tuple 条件定义解析器，用于把 conditionName 解析为 conditionDefinitionId。
     */
    @Resource
    private TupleConditionDefinitionResolver conditionDefinitionResolver;

    /**
     * 审计上下文提供者，用于在命令未显式携带审计信息时补齐当前调用上下文。
     */
    @Resource
    private IAuditContextProvider auditContextProvider;

    /**
     * 将写入命令转换为领域 tuple 写入请求。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识，用于解析条件定义
     * @param commands             写入命令列表
     * @return 领域 tuple 变更请求列表
     */
    List<TupleMutationRequest> toWriteRequests(String storeId, String authorizationModelId,
                                               List<WriteTupleCommand> commands) {
        Map<String, Long> conditionIds = conditionDefinitionResolver.resolve(storeId, authorizationModelId, commands);
        return commands.stream()
                .map(command -> TupleMutationRequest.of(
                        buildTupleKey(command),
                        buildTupleCondition(command, conditionIds),
                        command.getExpiresAt(),
                        resolveAuditMetadata(command)))
                .toList();
    }

    /**
     * 将删除命令转换为领域 tuple 删除请求。
     *
     * <p>删除语义只依赖 tuple key，不依赖 tuple 条件快照，因此这里不会解析模型条件定义。
     *
     * @param commands 删除命令列表
     * @return 领域 tuple 变更请求列表
     */
    List<TupleMutationRequest> toDeleteRequests(List<WriteTupleCommand> commands) {
        return commands.stream()
                .map(command -> TupleMutationRequest.of(
                        buildTupleKey(command),
                        TupleCondition.EMPTY,
                        command.getExpiresAt(),
                        resolveAuditMetadata(command)))
                .toList();
    }

    /**
     * 根据写入命令构建 tuple key。
     *
     * @param command 应用命令
     * @return tuple key
     */
    private TupleKey buildTupleKey(WriteTupleCommand command) {
        return TupleKey.of(
                command.getObjectType(),
                command.getObjectId(),
                command.getRelation(),
                command.getSubjectType(),
                command.getSubjectId(),
                command.getSubjectRelation());
    }

    /**
     * 构建 tuple 条件值对象。
     *
     * @param command      写入命令
     * @param conditionIds 条件名称到条件定义 ID 的映射
     * @return tuple 条件值对象
     */
    private TupleCondition buildTupleCondition(WriteTupleCommand command, Map<String, Long> conditionIds) {
        return TupleCondition.of(conditionDefinitionResolver.resolveConditionDefinitionId(command, conditionIds),
                command.getConditionName(),
                command.getConditionContext());
    }

    /**
     * 解析写入命令中的审计元数据。
     *
     * @param command 应用命令
     * @return 审计元数据
     */
    private AuditMetadata resolveAuditMetadata(WriteTupleCommand command) {
        WriteTupleCommand.AuditMetadataInput auditMetadata = command.getAuditMetadata();
        if (auditMetadata != null) {
            return AuditMetadata.of(auditMetadata.getOperatorId(), auditMetadata.getRequestId(), auditMetadata.getSource());
        }
        return auditContextProvider.current();
    }
}
