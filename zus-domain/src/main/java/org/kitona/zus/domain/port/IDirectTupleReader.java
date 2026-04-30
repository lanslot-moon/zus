package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;

import java.util.List;

/**
 * 直接 tuple 读取端口。
 * 该接口定义了直接读取关系元组的方法。
 */
public interface IDirectTupleReader {

    /**
     * 根据指定条件查找直接关系元组
     *
     * @param storeId   存储标识符，用于指定特定的存储位置
     * @param object    对象引用，表示关系中的起始对象
     * @param relation  关系名称，表示对象间的关系类型
     * @param maxZookie 最大版本号，用于限制返回的元组版本范围
     * @return 匹配的关系元组列表，如果没有匹配则返回空列表
     */
    List<RelationTuple> findDirectTuples(String storeId, ObjectRef object, String relation, Long maxZookie);
}
