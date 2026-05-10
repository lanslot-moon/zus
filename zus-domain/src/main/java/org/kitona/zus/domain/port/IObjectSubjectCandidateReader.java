package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 object 反查 subject 候选端口。
 *
 * <p>该端口根据 ListSubjects 一定存在的 store、object、relation 与一致性边界缩小 subject 候选集。
 * subjectType 和 subjectRelation 属于可选裁剪条件，应由搜索领域服务在候选收集后过滤。
 *
 * @author CodeGeeX
 * @version 1.0
 */
public interface IObjectSubjectCandidateReader {

    /**
     * 根据对象和关系获取主体候选列表。
     *
     * @param storeId    存储ID
     * @param objectType 对象类型
     * @param objectId   对象标识
     * @param relation   关系名称
     * @param maxZookie  最大Zookie值
     * @return 返回关系元组列表(List<RelationTuple>)，包含所有符合条件的主题候选信息
     */
    List<RelationTuple> listSubjectCandidates(String storeId, String objectType, String objectId, String relation,
                                              Long maxZookie);
}
