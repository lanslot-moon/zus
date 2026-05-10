package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 subject 反查 object 候选端口。
 *
 * <p>该端口根据 ListObjects 一定存在的 store、subject、relation 与一致性边界缩小 object 候选集。
 * objectType 属于可选裁剪条件，应由搜索领域服务在候选收集后过滤。
 */
public interface ISubjectObjectCandidateReader {

    /**
     * 根据主体和关系获取对象候选列表。
     *
     * @param storeId     存储ID
     * @param subjectType 主体类型
     * @param subjectId   主体标识
     * @param relation    关系名称
     * @param maxZookie   最大Zookie值
     * @return 返回满足条件的RelationTuple列表，每个RelationTuple代表一个对象候选
     */
    List<RelationTuple> listObjectCandidates(String storeId, String subjectType, String subjectId, String relation, Long maxZookie);
}
