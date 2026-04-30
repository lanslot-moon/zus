package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;

import java.util.List;

/**
 * TTU 链接 tuple 读取端口。
 * 该接口定义了读取关系元组(RelationTuple)的方法。
 */
public interface ITupleLinkReader {

    /**
     * 根据指定条件查找关系元组。
     *
     * @param storeId   存储ID，用于标识特定的数据存储
     * @param object    对象引用，表示要查询的源对象
     * @param relation  关系类型，描述对象之间的关系
     * @param maxZookie 最大版本号，用于获取指定版本之前的数据
     * @return 符合条件的关系元组列表
     */
    List<RelationTuple> findTupleLinks(String storeId, ObjectRef object, String relation, Long maxZookie);
}
