package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;

import java.util.List;

/**
 * TTU 链接 tuple 读取端口。
 */
public interface ITupleLinkReader {

    List<RelationTuple> findTupleLinks(String storeId, ObjectRef object, String relation, Long maxZookie);
}
