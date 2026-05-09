package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleKeyQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleSubjectQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.WildcardTupleExistsQuery;

import java.util.List;
import java.util.Optional;

/**
 * 关系元组持久化仓储接口（基础设施层）
 *
 * <p>定义基于 PO 的持久化契约，与领域层
 * {@link org.kitona.zus.domain.repository.ITupleDomainRepository} /
 * {@link org.kitona.zus.domain.repository.ITupleQueryRepository} 职责不同：
 * <ul>
 *   <li>领域层接口：操作领域实体（RelationTuple），定义业务契约</li>
 *   <li>本接口：操作持久化对象（TuplePO），定义技术契约</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface ITuplePersistenceRepository extends IService<TuplePO> {

    /**
     * 检查指定元组是否存在，支持按 maxZookie 做一致性读取
     *
     * @param query 元组存在性查询参数（storeId、object、relation、subject、maxZookie）
     * @return 存在返回 true
     */
    boolean existsTuple(TupleExistsQuery query);

    /**
     * 检查是否存在通配符元组（如 subjectId 为 * 的授权）
     *
     * @param query 通配符元组存在性查询参数（storeId、object、relation、subjectId、maxZookie）
     * @return 存在返回 true
     */
    boolean existsWildcardTuple(WildcardTupleExistsQuery query);

    /**
     * 按资源与关系查询元组列表，用于 TTU 等推导
     *
     * @param storeId    存储空间 ID
     * @param objectType 资源类型
     * @param objectId   资源 ID
     * @param relation   关系名
     * @param maxZookie  最大 Zookie，传 null 表示读最新
     * @return 元组列表，无则返回空列表
     */
    List<TuplePO> findByObjectAndRelation(String storeId, String objectType,
                                          String objectId, String relation, Long maxZookie);

    /**
     * 按主体查询元组列表，用于 ListObjects 等，可按 objectType、relation 过滤
     *
     * @param query 按主体查询参数（storeId、subject、objectType、relation、maxZookie）
     * @return 元组列表，无则返回空列表
     */
    List<TuplePO> findBySubject(TupleSubjectQuery query);

    /**
     * 按资源查询元组列表，用于 ListSubjects 等，可按 relation 过滤
     *
     * @param storeId    存储空间 ID
     * @param objectType 资源类型
     * @param objectId   资源 ID
     * @param relation   关系名过滤，可选
     * @param maxZookie  最大 Zookie，传 null 表示读最新
     * @return 元组列表，无则返回空列表
     */
    List<TuplePO> findByObject(String storeId, String objectType, String objectId,
                               String relation, Long maxZookie);

    /**
     * 按完整元组键查询单条元组
     *
     * @param query 元组键查询参数（storeId、object、relation、subject）
     * @return 存在则返回 Optional 包装的 TuplePO，否则 empty
     */
    Optional<TuplePO> findByTupleKey(TupleKeyQuery query);

    /**
     * 批量插入元组
     *
     * @param tuples 元组列表
     * @return 实际插入行数
     */
    boolean batchCreate(List<TuplePO> tuples);

    /**
     * 按 ID 批量逻辑删除元组
     *
     * @param storeId 存储空间 ID
     * @param ids    元组主键 ID 列表
     * @return 实际更新行数
     */
    boolean batchDelete(String storeId, List<Long> ids);

    /**
     * 分页查询元组列表，按主键 ID 升序，pageToken 为上一页最后一条 ID
     *
     * @param storeId    存储空间 ID
     * @param objectType 资源类型过滤，可选
     * @param relation   关系名过滤，可选
     * @param pageSize   每页条数
     * @param pageToken  分页游标，上一页最后一条 id，首页传 null
     * @return 元组列表
     */
    List<TuplePO> listTuples(String storeId, String objectType, String relation, int pageSize, Long pageToken);

    /**
     * 根据多个元组键批量查询（避免 N+1 问题）
     *
     * @param storeId       存储空间 ID
     * @param tupleKeyQueries 元组键查询参数列表
     * @return 存在的元组列表
     */
    List<TuplePO> findByTupleKeys(String storeId, List<TupleKeyQuery> tupleKeyQueries);

    /**
     * 分页列出关系元组（支持完整过滤条件）
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
     * @return 关系元组 PO 列表
     */
    List<TuplePO> listTuplesWithFilter(String storeId, String objectType, String objectId,
                                       String relation, String subjectType, String subjectId,
                                       String subjectRelation, int pageSize, Long pageToken,
                                       Long maxZookie);
}
