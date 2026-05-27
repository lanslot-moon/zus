package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
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

    /**
     * tuple 变更领域服务，用于构建关系 tuple 与 changelog 聚合对象。
     */
    @Resource
    private TupleMutationDomainService tupleMutationDomainService;

    /**
     * tuple 聚合仓储，用于保存或删除关系事实。
     */
    @Resource
    private ITupleDomainRepository tupleRepository;

    /**
     * changelog 聚合仓储，用于持久化 Watch 与审计所需的变更事实。
     */
    @Resource
    private IChangelogDomainRepository changelogRepository;

    /**
     * zookie 序列端口，用于为一次 tuple 变更分配新的全局版本。
     */
    @Resource
    private IZookieSequencePort zookieSequencePort;

    /**
     * tuple 变更请求装配器，用于把应用命令转换为领域请求。
     */
    @Resource
    private TupleMutationRequestAssembler tupleMutationRequestAssembler;

    /**
     * 写入关系元组。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识，用于解析 tuple 条件定义
     * @param commands             写入命令列表
     * @return 执行结果
     */
    public TupleMutationOutcome write(String storeId, String authorizationModelId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        List<TupleMutationRequest> requests =
                tupleMutationRequestAssembler.toWriteRequests(storeId, authorizationModelId, commands);
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
     * 删除关系元组。
     *
     * @param storeId Store 标识
     * @param commands 删除命令列表
     * @return 执行结果
     */
    public TupleMutationOutcome delete(String storeId, List<WriteTupleCommand> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return TupleMutationOutcome.empty();
        }
        List<TupleMutationRequest> requests = tupleMutationRequestAssembler.toDeleteRequests(commands);
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
     * 生成下一版本 zookie。
     *
     * @param storeId Store 标识
     * @return 下一版本 zookie
     */
    private Zookie nextZookie(String storeId) {
        return Zookie.of(zookieSequencePort.nextZookie(storeId));
    }
}
