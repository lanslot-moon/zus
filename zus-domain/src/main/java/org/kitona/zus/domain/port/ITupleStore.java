package org.kitona.zus.domain.port;

import org.kitona.zus.domain.valueobject.RelationTuple;

import java.util.Set;

/**
 * 元组存储端口
 * <p>
 * 供权限检查器按需查询元组。
 * 该接口抽象了元组的存储方式，支持多种实现：
 * <ul>
 *   <li>内存实现：用于单元测试或小规模数据</li>
 *   <li>仓储实现：用于生产环境，从数据库查询</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleStore {

    /**
     * 检查是否存在指定的直接元组
     *
     * @param subjectType  主体类型，例如 user
     * @param subjectId    主体标识，例如 alice
     * @param resourceType 资源类型，例如 document
     * @param resourceId   资源标识，例如 99
     * @param relationName 关系名称，例如 viewer
     * @return 存在返回 true
     */
    boolean hasTuple(String subjectType, String subjectId, String resourceType, String resourceId, String relationName);

    /**
     * 查找某资源某关系下的所有元组（用于 TTU 等场景）
     *
     * @param resourceType 资源类型
     * @param resourceId   资源标识
     * @param relationName 关系名称
     * @return 链接到的所有元组集合
     */
    Set<RelationTuple> findTuples(String resourceType, String resourceId, String relationName);
}
