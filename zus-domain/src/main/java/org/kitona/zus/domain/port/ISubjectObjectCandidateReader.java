package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 subject 反查 object 候选端口。
 */
public interface ISubjectObjectCandidateReader {

    List<RelationTuple> listObjectCandidates(String storeId, String objectType, Long maxZookie);
}
