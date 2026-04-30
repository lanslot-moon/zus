package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

import java.util.List;

/**
 * 从 subject 反查 object 候选端口。
 * 该接口定义了一个方法，用于根据给定的条件获取对象候选列表。
 */
public interface ISubjectObjectCandidateReader {

    /**
     * 根据存储ID、对象类型和最大Zookie值获取对象候选列表。
     *
     * @param storeId    存储ID，用于标识特定的数据存储
     * @param objectType 对象类型，指定要查询的对象类别
     * @param maxZookie  最大Zookie值，用于筛选符合条件的记录
     * @return 返回满足条件的RelationTuple列表，每个RelationTuple代表一个对象候选
     */
    List<RelationTuple> listObjectCandidates(String storeId, String objectType, Long maxZookie);
}
