package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;

import java.util.List;

/**
 * 直接 tuple 读取端口。
 */
public interface IDirectTupleReader {

    List<RelationTuple> findDirectTuples(String storeId, ObjectRef object, String relation, Long maxZookie);
}
