package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
import org.kitona.zus.domain.port.IAuditContextProvider;
import org.kitona.zus.domain.port.IZookieSequencePort;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.service.TupleMutationDomainService;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * tuple 变更编排器
 *
 * <p>应用层仅负责将命令对象转换为领域可理解的变更请求，
 * 并协调 zookie 生成、仓储保存和 changelog 持久化。
 */
@Component
public class TupleMutationCoordinator {

    @Resource
    private TupleMutationDomainService tupleMutationDomainService;

    @Resource
    private ITupleDomainRepository tupleRepository;

    @Resource
    private IChangelogDomainRepository changelogRepository;

    @Resource
    private IZookieSequencePort zookieSequencePort;

    @Resource
    private IAuditContextProvider auditContextProvider;

    /**
     * 写入write。
     *
     * @param storeId Store 标识
     * @param commands commands 参数
     * @return 执行结果
     */
    public TupleMutationOutcome write(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        List<TupleMutationRequest> requests = toRequests(commands);
        Zookie zookie = nextZookie(storeId);
        List<RelationTuple> tuples = tupleMutationDomainService.buildWriteTuples(storeId, requests, zookie);
        tuples.forEach(tupleRepository::save);

        List<TupleKey> writtenKeys = tuples.stream().map(RelationTuple::getTupleKey).toList();
        AuditMetadata auditMetadata = requests.get(0).auditMetadata();
        List<Changelog> changelogs = tupleMutationDomainService.buildChangelogs(
                storeId, writtenKeys, zookie, Changelog.OPERATION_WRITE, auditMetadata);
        changelogRepository.saveAll(changelogs);
        return new TupleMutationOutcome(zookie, writtenKeys, List.of(), auditMetadata);
    }

    /**
     * 删除delete。
     *
     * @param storeId Store 标识
     * @param commands commands 参数
     * @return 执行结果
     */
    public TupleMutationOutcome delete(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        List<TupleMutationRequest> requests = toRequests(commands);
        List<RelationTuple> existingTuples = requests.stream()
                .map(request -> tupleRepository.findByKey(storeId, request.tupleKey()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        if (existingTuples.isEmpty()) {
            return TupleMutationOutcome.empty();
        }

        Zookie zookie = nextZookie(storeId);
        existingTuples.forEach(tupleRepository::remove);
        List<TupleKey> deletedKeys = existingTuples.stream().map(RelationTuple::getTupleKey).toList();
        AuditMetadata auditMetadata = requests.get(0).auditMetadata();
        List<Changelog> changelogs = tupleMutationDomainService.buildChangelogs(
                storeId, deletedKeys, zookie, Changelog.OPERATION_DELETE, auditMetadata);
        changelogRepository.saveAll(changelogs);
        return new TupleMutationOutcome(zookie, List.of(), deletedKeys, auditMetadata);
    }

    /**
     * 将写入命令转换为 tuple 变更请求。
     *
     * @param commands commands 参数
     * @return 构建结果
     */
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

    /**
     * 根据写入命令构建 tuple key。
     *
     * @param command 应用命令
     * @return 构建结果
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
     * 解析写入命令中的审计元数据。
     *
     * @param command 应用命令
     * @return 构建结果
     */
    private AuditMetadata resolveAuditMetadata(WriteTupleCommand command) {
        WriteTupleCommand.AuditMetadataInput auditMetadata = command.getAuditMetadata();
        if (auditMetadata != null) {
            return AuditMetadata.of(auditMetadata.getOperatorId(), auditMetadata.getRequestId(), auditMetadata.getSource());
        }
        return auditContextProvider.current();
    }

    /**
     * 生成next zookie。
     *
     * @param storeId Store 标识
     * @return 返回结果
     */
    private Zookie nextZookie(String storeId) {
        return Zookie.of(zookieSequencePort.nextZookie(storeId));
    }
}
