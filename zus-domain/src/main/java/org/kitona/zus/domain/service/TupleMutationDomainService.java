package org.kitona.zus.domain.service;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.entity.ChangelogEntity;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.port.IZookieSequencePort;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.TupleMutationRequest;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 变更领域服务
 *
 * <p>集中处理 tuple 写入/删除的核心业务规则：
 * 生成 zookie、构造 tuple、决定实际删除集合，并同步落库 changelog。
 */
public final class TupleMutationDomainService {

    private final ITupleDomainRepository tupleRepository;
    private final IChangelogDomainRepository changelogRepository;
    private final IZookieSequencePort zookieSequencePort;

    public TupleMutationDomainService(ITupleDomainRepository tupleRepository,
                                      IChangelogDomainRepository changelogRepository,
                                      IZookieSequencePort zookieSequencePort) {
        this.tupleRepository = tupleRepository;
        this.changelogRepository = changelogRepository;
        this.zookieSequencePort = zookieSequencePort;
    }

    public TupleMutationResult write(String storeId, List<TupleMutationRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return TupleMutationResult.empty();
        }

        Zookie newZookie = nextZookie(storeId);
        List<RelationTupleEntity> tupleEntities = requests.stream()
                .map(request -> RelationTupleEntity.create(storeId, request.tupleKey(), newZookie, request.condition()))
                .toList();
        tupleRepository.saveBatch(tupleEntities);

        List<TupleKey> writtenKeys = tupleEntities.stream()
                .map(RelationTupleEntity::getTupleKey)
                .toList();
        saveChangelogBatch(storeId, writtenKeys, newZookie, ChangelogEntity.OPERATION_WRITE);
        return new TupleMutationResult(newZookie, writtenKeys, Collections.emptyList());
    }

    public TupleMutationResult delete(String storeId, List<TupleMutationRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            return TupleMutationResult.empty();
        }

        List<TupleKey> requestedKeys = requests.stream()
                .map(TupleMutationRequest::tupleKey)
                .toList();
        List<RelationTupleEntity> existingTuples = tupleRepository.findByTupleKeys(storeId, requestedKeys);
        if (CollectionUtils.isEmpty(existingTuples)) {
            return TupleMutationResult.empty();
        }

        Zookie newZookie = nextZookie(storeId);
        List<Long> ids = existingTuples.stream()
                .map(RelationTupleEntity::getId)
                .toList();
        tupleRepository.batchDelete(storeId, ids);

        List<TupleKey> deletedKeys = existingTuples.stream()
                .map(RelationTupleEntity::getTupleKey)
                .toList();
        saveChangelogBatch(storeId, deletedKeys, newZookie, ChangelogEntity.OPERATION_DELETE);
        return new TupleMutationResult(newZookie, Collections.emptyList(), deletedKeys);
    }

    private Zookie nextZookie(String storeId) {
        return Zookie.of(zookieSequencePort.nextZookie(storeId));
    }

    private void saveChangelogBatch(String storeId, List<TupleKey> keys, Zookie zookie, String operation) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        Long zookieVersion = zookie != null && zookie.getVersion() != null ? zookie.getVersion() : 0L;
        List<ChangelogEntity> changelogs = keys.stream()
                .map(key -> buildChangelogEntity(storeId, key, zookieVersion, operation))
                .toList();
        changelogRepository.saveBatch(changelogs);
    }

    private ChangelogEntity buildChangelogEntity(String storeId, TupleKey key, Long zookieVersion, String operation) {
        if (ChangelogEntity.OPERATION_WRITE.equals(operation)) {
            return ChangelogEntity.createWriteLog(storeId, key, zookieVersion);
        }
        return ChangelogEntity.createDeleteLog(storeId, key, zookieVersion);
    }
}
