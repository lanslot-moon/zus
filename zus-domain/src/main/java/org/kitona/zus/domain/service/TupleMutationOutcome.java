package org.kitona.zus.domain.service;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 变更结果
 *
 * <p>表达一次权限关系变更后真正发生的写入/删除事实。
 */
public record TupleMutationOutcome(Zookie zookie, List<TupleKey> writtenKeys, List<TupleKey> deletedKeys,
                                   AuditMetadata auditMetadata) {

    public TupleMutationOutcome {
        writtenKeys = writtenKeys == null ? Collections.emptyList() : List.copyOf(writtenKeys);
        deletedKeys = deletedKeys == null ? Collections.emptyList() : List.copyOf(deletedKeys);
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
