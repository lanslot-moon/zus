package org.kitona.zus.domain.read.criteria;

import org.kitona.zus.domain.authorization.tuple.TupleKey;

import java.util.List;

/**
 * tuple key 查询条件。
 *
 * @param storeId 存储空间
 * @param tupleKeys tuple keys
 */
public record TupleKeyCriteria(String storeId, List<TupleKey> tupleKeys) {
}
