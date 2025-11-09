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
     * @param user         用户ID (user:...) 或 对象ID (folder:1)
     * @param resource     资源实例ID (document:99)
     * @param relationName 关系名称 (writer)
     * @return 是否存在
     */
    public boolean checkDirectTuple(String user, String resource, String relationName) {
        return tuples.stream().anyMatch(t ->
                t.user().equals(user) &&
                        t.relationName().equals(relationName) &&
                        (t.resourceType() + ":" + t.resourceId()).equals(resource)
        );
    }

    /**
     * 查找某个资源实例上特定关系的所有直接链接对象
     * 对应 TTU 规则的 from=document#parentFolder 的元组查找
     *
     * @param resource     资源的实例ID (document:99)
     * @param relationName 链接关系 (parentFolder)
     * @return 链接到的所有对象ID (例如 [folder:1])
     */
    public List<String> findLinkedResources(String resource, String relationName) {
        return tuples.stream()
                .filter(t -> t.relationName().equals(relationName) && (t.resourceType() + ":" + t.resourceId()).equals(resource))
                .map(RelationTuple::user) // 在 TTU 中，user 字段存储的是链接对象 ID
                .toList();
    }

    public boolean hasTuple(String user, String resourceType, String resourceId, String relation) {
        return tuples.stream().anyMatch(t ->
                t.user().equals(user) && t.resourceType().equals(resourceType) && t.resourceId().equals(resourceId) && t.relationName().equals(relation)
        );
    }

    public Set<RelationTuple> findTuples(String resourceId, String relationName) {
        return tuples.stream()
                .filter(t -> t.resourceId().equals(resourceId) && t.relationName().equals(relationName))
                .collect(Collectors.toSet());
    }
}