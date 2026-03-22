package org.kitona.zus.service.application.coordinator;

import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Collections;
import java.util.List;

/**
 * tuple 写删编排结果
 *
 * @param zookie      本次操作生成的 zookie
 * @param writtenKeys 本次写入的 tuple key 列表
 * @param deletedKeys 本次删除的 tuple key 列表
 */
public record TupleWriteResult(Zookie zookie, List<TupleKey> writtenKeys, List<TupleKey> deletedKeys) {

    public TupleWriteResult {
        writtenKeys = writtenKeys != null ? List.copyOf(writtenKeys) : Collections.emptyList();
        deletedKeys = deletedKeys != null ? List.copyOf(deletedKeys) : Collections.emptyList();
    }

    public static TupleWriteResult empty() {
        return new TupleWriteResult(Zookie.EMPTY, Collections.emptyList(), Collections.emptyList());
    }

    public boolean hasWrites() {
        return !writtenKeys.isEmpty();
    }

    public boolean hasDeletes() {
        return !deletedKeys.isEmpty();
    }
}
