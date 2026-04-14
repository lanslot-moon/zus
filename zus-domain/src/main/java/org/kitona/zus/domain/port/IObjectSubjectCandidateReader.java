package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 object 反查 subject 候选端口。
 */
public interface IObjectSubjectCandidateReader {

    List<RelationTuple> listSubjectCandidates(String storeId, Long maxZookie);
}
