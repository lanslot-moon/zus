package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 object 反查 subject 候选端口。
 * 这是一个接口定义，用于根据对象(oid)查询相关的主题(subject)候选列表。
 *
 * @author CodeGeeX
 * @version 1.0
 */
public interface IObjectSubjectCandidateReader {

    /**
     * 根据存储ID和最大Zookie值获取主题候选列表
     *
     * @param storeId 存储标识符，用于标识特定的数据存储
     * @param maxZookie 最大Zookie值，用于查询条件过滤
     * @return 返回关系元组列表(List<RelationTuple>)，包含所有符合条件的主题候选信息
     */
    List<RelationTuple> listSubjectCandidates(String storeId, Long maxZookie);
}
