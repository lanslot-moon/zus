package org.kitona.zus.business.entity.bo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 元组存储（模拟数据层）
 */
public record TupleStore(List<RelationTuple> tuples) {

    /**
     * 检查是否存在指定的直连元组
     *
     * @param relationName 关系名称 (writer)
     * @param resourceType 资源类型 (document)
     * @param resourceId   资源实例ID (99)
     * @param subjectType  对象类型 (folder)
     * @param subjectId    对象实例ID (1)
     * @return 是否存在
     */
    public boolean hasTuple(String subjectType, String subjectId, String resourceType, String resourceId, String relationName) {
        return tuples.stream().anyMatch(t ->
                t.subjectType().equals(subjectType) && t.subjectId().equals(subjectId) &&
                        t.resourceType().equals(resourceType) && t.resourceId().equals(resourceId)
                        && t.relationName().equals(relationName)
        );
    }

    /**
     * 查找某个资源实例上特定关系的所有直接链接对象
     * 对应 TTU 规则的 from=document#parentFolder 的元组查找
     *
     * @param resourceType 资源的类型 (document)
     * @param resourceId   资源实例ID (99)
     * @param relationName 链接关系 (parentFolder)
     * @return 链接到的所有对象ID (例如 [folder:1])
     */
    public Set<RelationTuple> findTuples(String resourceType, String resourceId, String relationName) {
        return tuples.stream()
                .filter(t -> t.relationName().equals(relationName) && t.resourceType().equals(resourceType)
                        && t.resourceId().equals(resourceId))
                .collect(Collectors.toSet());
    }
}