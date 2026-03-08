package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.valueobject.RelationTuple;
import org.kitona.zus.domain.valueobject.TupleKey;

import java.util.List;
import java.util.Optional;

/**
 * 关系元组仓储接口（领域层）
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
 * <p>按照 DDD 严格规范，Repository 操作领域实体 {@link RelationTupleEntity}。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleDomainRepository {

    /**
     * 检查是否存在指定的直接元组
     * 
     * <p>直接元组是指主体直接拥有某资源某关系的元组，不包含 userset 展开。
     *
     * @param storeId         存储空间ID
     * @param objectType      资源对象类型
     * @param objectId        资源对象ID
     * @param relation        关系名称
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系（userset 场景），普通用户为 null
     * @param maxZookie       一致性令牌，只查询该版本之前的元组
     * @return 存在返回 true
     */
    boolean existsTuple(String storeId, String objectType, String objectId, String relation,
                        String subjectType, String subjectId, String subjectRelation, Long maxZookie);

    /**
     * 检查是否存在通配符元组
     * 
     * <p>通配符元组表示所有用户都拥有某关系，subjectId 为 "*"。
     * 例如：{@code document:readme#viewer@user:*} 表示所有用户都是 readme 的 viewer。
     *
     * @param storeId    存储空间ID
     * @param objectType 资源对象类型
     * @param objectId   资源对象ID
     * @param relation   关系名称
     * @param maxZookie  一致性令牌
     * @return 存在返回 true
     */
    boolean existsWildcardTuple(String storeId, String objectType, String objectId, String relation, Long maxZookie);

    /**
     * 根据资源对象和关系查询所有元组
     * 
     * <p>返回轻量级的 {@link RelationTuple} 用于权限检查引擎。
     *
     * @param storeId    存储空间ID
     * @param objectType 资源对象类型
     * @param objectId   资源对象ID
     * @param relation   关系名称
     * @param maxZookie  一致性令牌
     * @return 元组列表
     */
    List<RelationTupleEntity> findByObjectAndRelation(String storeId, String objectType, String objectId,
                                                  String relation, Long maxZookie);

    /**
     * 分页列出关系元组（基础方法）
     *
     * @param storeId    存储空间ID
     * @param objectType 资源对象类型过滤，可为 null
     * @param relation   关系名称过滤，可为 null
     * @param pageSize   每页数量
     * @param pageToken  分页游标（上一页最后一条的 zookie），首页传 null
     * @return 关系元组实体列表
     */
    List<RelationTupleEntity> listTuples(String storeId, String objectType, String relation, int pageSize, Long pageToken);

    /**
     * 分页列出关系元组（支持完整过滤条件）
     *
     * <p>支持按 object、relation、subject 进行组合过滤，过滤逻辑在 Repository 层完成。
     *
     * @param storeId         存储空间ID
     * @param objectType      资源对象类型过滤，可为 null
     * @param objectId        资源对象ID过滤，可为 null
     * @param relation        关系名称过滤，可为 null
     * @param subjectType     主体类型过滤，可为 null
     * @param subjectId       主体ID过滤，可为 null
     * @param subjectRelation 主体关系过滤，可为 null
     * @param pageSize        每页数量
     * @param pageToken       分页游标（上一页最后一条的ID），首页传 null
     * @return 关系元组实体列表
     */
    List<RelationTupleEntity> listTuplesWithFilter(String storeId, String objectType, String objectId,
                                                    String relation, String subjectType, String subjectId,
                                                    String subjectRelation, int pageSize, Long pageToken);

    /**
     * 根据主体查询元组（反向查询）
     * 
     * <p>查询某主体在哪些资源上拥有哪些关系。
     *
     * @param storeId         存储空间ID
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系（userset 场景），普通用户为 null
     * @param objectType      资源对象类型过滤，可为 null
     * @param relation        关系名称过滤，可为 null
     * @param maxZookie       一致性令牌
     * @return 关系元组实体列表
     */
    List<RelationTupleEntity> findBySubject(String storeId, String subjectType, String subjectId, String subjectRelation,
                                 String objectType, String relation, Long maxZookie);

    /**
     * 根据资源对象查询元组
     *
     * @param storeId    存储空间ID
     * @param objectType 资源对象类型
     * @param objectId   资源对象ID
     * @param relation   关系名称过滤，可为 null
     * @param maxZookie  一致性令牌
     * @return 关系元组实体列表
     */
    List<RelationTupleEntity> findByObject(String storeId, String objectType, String objectId, String relation, Long maxZookie);

    /**
     * 批量保存关系元组
     *
     * @param tuples 关系元组实体列表
     * @return 保存成功返回 true
     */
    boolean saveBatch(List<RelationTupleEntity> tuples);

    /**
     * 根据元组键精确查询（唯一性查询）
     * 
     * <p>元组键由 object + relation + subject 组成，在同一个 store 内唯一。
     *
     * @param storeId         存储空间ID
     * @param objectType      资源对象类型
     * @param objectId        资源对象ID
     * @param relation        关系名称
     * @param subjectType     主体类型
     * @param subjectId       主体ID
     * @param subjectRelation 主体关系，可为 null
     * @return 关系元组实体，不存在返回 empty
     */
    Optional<RelationTupleEntity> findByTupleKey(String storeId, String objectType, String objectId, String relation,
                                      String subjectType, String subjectId, String subjectRelation);

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
     * @param storeId   存储空间ID
     * @param tupleKeys 元组键列表
     * @return 存在的关系元组实体列表
     */
    List<RelationTupleEntity> findByTupleKeys(String storeId, List<TupleKey> tupleKeys);
}
