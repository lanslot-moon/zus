package org.kitona.zus.service.application.coordinator;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 变更编排结果
 *
 * @param zookie      本次操作生成的 zookie
 * @param writtenKeys 本次写入的 tuple key 列表
 * @param deletedKeys 本次删除的 tuple key 列表
 */
public record TupleMutationOutcome(Zookie zookie, List<TupleKey> writtenKeys, List<TupleKey> deletedKeys,
                                   AuditMetadata auditMetadata) {

    public TupleMutationOutcome {
        writtenKeys = writtenKeys != null ? List.copyOf(writtenKeys) : Collections.emptyList();
        deletedKeys = deletedKeys != null ? List.copyOf(deletedKeys) : Collections.emptyList();
        auditMetadata = auditMetadata != null ? auditMetadata : AuditMetadata.EMPTY;
    }

    public static TupleMutationOutcome empty() {
        return new TupleMutationOutcome(Zookie.EMPTY, Collections.emptyList(), Collections.emptyList(), AuditMetadata.EMPTY);
    }

    public boolean hasWrites() {
        return !writtenKeys.isEmpty();
    }

    public boolean hasDeletes() {
        return !deletedKeys.isEmpty();
    }
}
