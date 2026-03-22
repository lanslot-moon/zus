package org.kitona.zus.domain.service;

import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 变更结果
 *
 * <p>表达一次权限关系变更后真正发生的写入/删除事实。
 */
public record TupleMutationResult(Zookie zookie, List<TupleKey> writtenKeys, List<TupleKey> deletedKeys) {

    public TupleMutationResult {
        writtenKeys = writtenKeys == null ? Collections.emptyList() : List.copyOf(writtenKeys);
        deletedKeys = deletedKeys == null ? Collections.emptyList() : List.copyOf(deletedKeys);
    }

    public static TupleMutationResult empty() {
        return new TupleMutationResult(Zookie.EMPTY, Collections.emptyList(), Collections.emptyList());
    }

    public boolean hasWrites() {
        return !writtenKeys.isEmpty();
    }

    public boolean hasDeletes() {
        return !deletedKeys.isEmpty();
    }
}
