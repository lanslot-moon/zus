package org.kitona.zus.domain.service;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
import org.kitona.zus.domain.port.IZookieSequencePort;
import org.kitona.zus.domain.read.criteria.TupleKeyCriteria;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 变更领域服务
 *
 * <p>集中处理 tuple 写入/删除的核心业务规则：
 * 生成 zookie、构造 tuple、决定实际删除集合，并同步落库 changelog。
 *
 * <h3>架构设计说明：为什么只处理 Tuple 变更？</h3>
 * <p>在 FGA 模型中，Store、AuthorizationModel 和 Tuple 的生命周期与变更频率完全不同：
 * <ul>
 *   <li><b>Tuple（元组）</b>：高频业务数据。每次授权分配都会触发增删，且必须严格生成连续的 {@code zookie} 并强制落库到 {@code fga_tuple_changelog} 以支持强一致性 Check 和 Watch 流。</li>
 *   <li><b>AuthorizationModel（模型）</b>：低频元数据。一旦发布（Publish）即不可变，其变更不需要写入 Tuple Changelog。</li>
 *   <li><b>Store（空间）</b>：极低频租户数据。仅在租户生命周期变更时发生。</li>
 * </ul>
 * <p>为了遵循 DDD 的<b>高内聚、低耦合</b>与<b>单一职责原则</b>，本服务被设计为仅聚焦于核心的、高并发的 Tuple 变更逻辑。Store 和 Model 的变更由它们各自的聚合根及领域/应用服务独立处理，以此实现变更副作用的物理隔离。
 */
public record TupleMutationDomainService(ITupleDomainRepository tupleRepository,
                                         IChangelogDomainRepository changelogRepository,
                                         IZookieSequencePort zookieSequencePort) {

    /**
     * 写入一批 tuple，并为这批变更分配同一个新的 zookie。
     *
     * <p>这样可以保证一次批量写入在一致性令牌上表现为同一个变更批次。
     */
    public TupleMutationOutcome write(String storeId, List<TupleMutationRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return TupleMutationOutcome.empty();
        }

        Zookie newZookie = nextZookie(storeId);
        List<RelationTuple> tupleEntities = requests.stream()
                .map(request -> RelationTuple.create(storeId, request.tupleKey(), newZookie,
                        request.condition(), request.expiresAt()))
                .toList();
        tupleRepository.saveBatch(tupleEntities);

        List<TupleKey> writtenKeys = tupleEntities.stream()
                .map(RelationTuple::getTupleKey)
                .toList();
        AuditMetadata auditMetadata = requests.get(0).auditMetadata();
        saveChangelogBatch(storeId, writtenKeys, newZookie, Changelog.OPERATION_WRITE, auditMetadata);
        return new TupleMutationOutcome(newZookie, writtenKeys, Collections.emptyList(), auditMetadata);
    }

    /**
     * 删除一批 tuple。
     *
     * <p>删除前会先查询实际存在的 tuple，避免对不存在的数据重复写审计日志。
     */
    public TupleMutationOutcome delete(String storeId, List<TupleMutationRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return TupleMutationOutcome.empty();
        }

        List<TupleKey> requestedKeys = requests.stream()
                .map(TupleMutationRequest::tupleKey)
                .toList();
        List<RelationTuple> existingTuples = tupleRepository.findByTupleKeys(
                new TupleKeyCriteria(storeId, requestedKeys));
        if (CollectionUtils.isEmpty(existingTuples)) {
            return TupleMutationOutcome.empty();
        }

        Zookie newZookie = nextZookie(storeId);
        List<Long> ids = existingTuples.stream()
                .map(RelationTuple::getId)
                .toList();
        tupleRepository.batchDelete(storeId, ids);

        List<TupleKey> deletedKeys = existingTuples.stream()
                .map(RelationTuple::getTupleKey)
                .toList();
        AuditMetadata auditMetadata = requests.get(0).auditMetadata();
        saveChangelogBatch(storeId, deletedKeys, newZookie, Changelog.OPERATION_DELETE, auditMetadata);
        return new TupleMutationOutcome(newZookie, Collections.emptyList(), deletedKeys, auditMetadata);
    }

    /**
     * 生成下一次 tuple 变更使用的一致性令牌。
     */
    private Zookie nextZookie(String storeId) {
        return Zookie.of(zookieSequencePort.nextZookie(storeId));
    }

    /**
     * 为一批 tuple 变更写入审计日志。
     */
    private void saveChangelogBatch(String storeId, List<TupleKey> keys, Zookie zookie,
                                    String operation, AuditMetadata auditMetadata) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        Long zookieVersion = zookie != null && zookie.getVersion() != null ? zookie.getVersion() : 0L;
        List<Changelog> changelogs = keys.stream()
                .map(key -> buildChangelogEntity(storeId, key, zookieVersion, operation, auditMetadata))
                .toList();
        changelogRepository.saveBatch(changelogs);
    }

    /**
     * 根据操作类型构造对应的领域审计对象。
     */
    private Changelog buildChangelogEntity(String storeId, TupleKey key, Long zookieVersion,
                                           String operation, AuditMetadata auditMetadata) {
        if (Changelog.OPERATION_WRITE.equals(operation)) {
            return Changelog.createWriteLog(storeId, key, zookieVersion, auditMetadata);
        }
        return Changelog.createDeleteLog(storeId, key, zookieVersion, auditMetadata);
    }
}
