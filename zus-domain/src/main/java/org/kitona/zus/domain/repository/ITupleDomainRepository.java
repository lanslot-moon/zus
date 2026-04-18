package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.read.criteria.TupleExistenceCriteria;
import org.kitona.zus.domain.read.criteria.TupleKeyCriteria;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;

import java.util.List;
import java.util.Optional;

/**
 * 关系元组聚合根仓储接口（领域层）
 * 
 * <p>关系元组(Relation Tuple)是 FGA 系统存储权限关系的基本单元。
 * 一个元组表示：<b>主体(Subject)</b> 与 <b>资源对象(Object)</b> 之间存在某种 <b>关系(Relation)</b>。
 * 
 * <p>元组结构：{@code object#relation@subject}
 * <ul>
 *   <li>object: 资源对象，格式为 {@code type:id}，如 document:readme</li>
 *   <li>relation: 关系名称，如 viewer、owner、parent</li>
 *   <li>subject: 主体，格式为 {@code type:id} 或 {@code type:id#relation}（userset）</li>
 * </ul>
 * 
 * <p>示例元组：
 * <ul>
 *   <li>{@code document:readme#viewer@user:alice} - alice 是 readme 的 viewer</li>
 *   <li>{@code document:readme#parent@folder:root} - readme 的 parent 是 root 文件夹</li>
 *   <li>{@code folder:root#viewer@group:engineering#member} - engineering 组的 member 是 root 的 viewer</li>
 * </ul>
 * 
 * <p>按照 DDD 规范，Repository 负责 tuple 聚合根 {@link RelationTuple} 的装载、
 * 规则相关查询和持久化。分页、过滤、列表类能力由独立查询仓储承接。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleDomainRepository {

    /**
     * 检查是否存在指定的直接元组
     *
     * <p>条件由 {@link TupleExistenceCriteria} 显式承载，避免接口暴露过长参数列表。
     *
     * @param criteria 直接元组存在性条件
     * @return 存在返回 true
     */
    boolean existsTuple(TupleExistenceCriteria criteria);

    /**
     * 检查是否存在通配符元组
     *
     * <p>通配符元组表示所有用户都拥有某关系，subjectId 为 "*"。
     * 例如：{@code document:readme#viewer@user:*} 表示所有用户都是 readme 的 viewer。
     *
     * @param criteria 通配符元组存在性条件
     * @return 存在返回 true
     */
    boolean existsWildcardTuple(TupleExistenceCriteria criteria);

    /**
     * 根据资源对象和关系查询所有元组
     *
     * <p>查询条件由 {@link TupleQueryCriteria} 承载，返回领域实体而不是轻量字符串描述。
     *
     * @param criteria 元组查询条件
     * @return 元组列表
     */
    List<RelationTuple> findByObjectAndRelation(TupleQueryCriteria criteria);

    /**
     * 批量保存关系元组
     *
     * @param tuples 关系元组实体列表
     * @return 保存成功返回 true
     */
    boolean saveBatch(List<RelationTuple> tuples);

    /**
     * 根据元组键精确查询（唯一性查询）
     *
     * <p>元组键由 object + relation + subject 组成，在同一个 store 内唯一。
     *
     * @param criteria 精确键查询条件
     * @return 关系元组实体，不存在返回 empty
     */
    Optional<RelationTuple> findByTupleKey(TupleExistenceCriteria criteria);

    /**
     * 批量删除关系元组
     *
     * @param storeId 存储空间ID
     * @param ids     元组ID列表
     * @return 删除成功返回 true
     */
    boolean batchDelete(String storeId, List<Long> ids);

    /**
     * 根据多个元组键批量查询（避免 N+1 问题）
     *
     * <p>通过一次数据库查询获取多个 TupleKey 对应的元组，用于批量删除场景。
     *
     * @param criteria 批量元组键查询条件
     * @return 存在的关系元组实体列表
     */
    List<RelationTuple> findByTupleKeys(TupleKeyCriteria criteria);
}
