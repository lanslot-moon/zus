package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;

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
 * <p>按照 DDD 规范，Repository 负责 tuple 聚合根 {@link RelationTuple} 的装载与持久化。
 * 是否存在、通配符匹配、对象/主体维度查询、批量删除等查询或技术批处理能力
 * 不在领域仓储接口中定义。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleDomainRepository {

    /**
     * 根据元组键装载关系元组聚合根。
     *
     * @param storeId  存储空间标识
     * @param tupleKey 元组键
     * @return 关系元组实体，不存在返回 empty
     */
    Optional<RelationTuple> findByKey(String storeId, TupleKey tupleKey);

    /**
     * 保存关系元组聚合根。
     *
     * @param tuple 关系元组聚合根
     */
    void save(RelationTuple tuple);

    /**
     * 移除关系元组聚合根。
     *
     * @param tuple 关系元组聚合根
     */
    void remove(RelationTuple tuple);
}
