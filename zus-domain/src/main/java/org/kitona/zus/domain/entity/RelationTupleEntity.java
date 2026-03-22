package org.kitona.zus.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.TupleCondition;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Objects;

/**
 * 关系元组聚合根
 *
 * <p>RelationTupleEntity 是以单条 tuple 为边界的聚合根，
 * 表示一条具体的权限关系，记录了“谁对什么资源有什么关系”。
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
 *   <li>{@code document:public#viewer@user:*} - 所有用户都是 public 文档的 viewer（通配符）</li>
 * </ul>
 *
 * <p>创建方式（符合 DDD）：
 * <ul>
 *   <li>业务创建：使用 {@link #create(String, TupleKey, Zookie)} 或
 *   {@link #create(String, TupleKey, Zookie, String, String)}</li>
 *   <li>持久化重建：使用
 *   {@link #reconstitute(Long, String, TupleKey, Zookie, String, String, Long)}（仅限基础设施层）</li>
 * </ul>
 *
 * <p>不变量（Invariant）：
 * <ul>
 *   <li>storeId 不能为空</li>
 *   <li>tupleKey 不能为空</li>
 *   <li>同一个 store 内，tupleKey（object + relation + subject）唯一</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode
@ToString
@Getter
public class RelationTupleEntity {

    /**
     * 元组ID（数据库主键）
     */
    private Long id;

    /**
     * 存储空间ID
     */
    private String storeId;

    /**
     * 元组键（object + relation + subject 的组合）
     */
    private TupleKey tupleKey;

    /**
     * Zookie 版本号（用于一致性控制）
     */
    private Zookie zookie;

    /**
     * 条件名称（可选，用于条件化权限）
     */
    private TupleCondition condition;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 私有构造函数，禁止外部直接 new，保证只能通过 create() 或 reconstitute() 创建
     */
    private RelationTupleEntity() {
    }

    // ========== 工厂方法（业务创建） ==========

    /**
     * 创建关系元组（业务场景）
     *
     * @param storeId  存储空间ID
     * @param tupleKey 元组键
     * @param zookie   Zookie 版本
     * @return RelationTupleEntity 实例
     * @throws NullPointerException 如果 storeId 或 tupleKey 为 null
     */
    public static RelationTupleEntity create(String storeId, TupleKey tupleKey, Zookie zookie) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(tupleKey, "tupleKey 不能为空");

        RelationTupleEntity tuple = new RelationTupleEntity();
        tuple.storeId = storeId;
        tuple.tupleKey = tupleKey;
        tuple.zookie = zookie != null ? zookie : Zookie.EMPTY;
        tuple.condition = TupleCondition.EMPTY;
        tuple.createTime = System.currentTimeMillis();
        return tuple;
    }

    /**
     * 创建带条件信息的关系元组（业务场景）
     *
     * @param storeId             存储空间ID
     * @param tupleKey            元组键
     * @param zookie              Zookie 版本
     * @param conditionName       条件名称
     * @param conditionContext    条件上下文
     * @return RelationTupleEntity 实例
     * @throws NullPointerException 如果 storeId 或 tupleKey 为 null
     */
    public static RelationTupleEntity create(String storeId, TupleKey tupleKey, Zookie zookie,
                                             String conditionName, String conditionContext) {
        return create(storeId, tupleKey, zookie, TupleCondition.of(conditionName, conditionContext));
    }

    public static RelationTupleEntity create(String storeId, TupleKey tupleKey, Zookie zookie,
                                             TupleCondition condition) {
        RelationTupleEntity tuple = create(storeId, tupleKey, zookie);
        tuple.condition = condition != null ? condition : TupleCondition.EMPTY;
        return tuple;
    }

    // ========== 重建方法（持久化恢复，仅供基础设施层使用） ==========

    /**
     * 从持久化数据重建实体（仅供基础设施层 Repository/Converter 使用，非业务创建入口）
     *
     * <p>此方法用于从数据库查询结果还原实体状态，不执行业务校验。
     *
     * @param id                  主键ID
     * @param storeId             存储空间ID
     * @param tupleKey            元组键
     * @param zookie              Zookie 版本
     * @param conditionName       条件名称
     * @param conditionContext    条件上下文
     * @param createTime          创建时间
     * @return 重建后的实体
     */
    public static RelationTupleEntity reconstitute(Long id, String storeId, TupleKey tupleKey,
                                                    Zookie zookie, String conditionName, String conditionContext,
                                                    Long createTime) {
        return reconstitute(id, storeId, tupleKey, zookie, TupleCondition.of(conditionName, conditionContext), createTime);
    }

    public static RelationTupleEntity reconstitute(Long id, String storeId, TupleKey tupleKey,
                                                   Zookie zookie, TupleCondition condition, Long createTime) {
        RelationTupleEntity tuple = new RelationTupleEntity();
        tuple.id = id;
        tuple.storeId = storeId;
        tuple.tupleKey = tupleKey;
        tuple.zookie = zookie != null ? zookie : Zookie.EMPTY;
        tuple.condition = condition != null ? condition : TupleCondition.EMPTY;
        tuple.createTime = createTime;
        return tuple;
    }

    // ========== 查询方法（委托给 TupleKey） ==========

    /**
     * 获取资源对象
     */
    public ObjectRef getObject() {
        return tupleKey.getObject();
    }

    /**
     * 获取资源类型
     */
    public String getObjectType() {
        return tupleKey.getObjectType();
    }

    /**
     * 获取资源ID
     */
    public String getObjectId() {
        return tupleKey.getObjectId();
    }

    /**
     * 获取关系名称
     */
    public String getRelation() {
        return tupleKey.getRelation();
    }

    /**
     * 获取主体
     */
    public Subject getSubject() {
        return tupleKey.getSubject();
    }

    /**
     * 获取主体类型
     */
    public String getSubjectType() {
        return tupleKey.getSubjectType();
    }

    /**
     * 获取主体ID
     */
    public String getSubjectId() {
        return tupleKey.getSubjectId();
    }

    /**
     * 获取主体关系（userset 场景）
     */
    public String getSubjectRelation() {
        return tupleKey.getSubjectRelation();
    }

    /**
     * 判断主体是否为用户集（userset）
     *
     * <p>用户集表示某对象某关系的所有主体，格式如 {@code group:engineering#member}
     *
     * @return 是用户集返回 true
     */
    public boolean hasUsersetSubject() {
        return tupleKey.getSubject().isUserset();
    }

    /**
     * 判断主体是否为通配符
     *
     * <p>通配符表示所有用户，格式如 {@code user:*}
     *
     * @return 是通配符返回 true
     */
    public boolean hasWildcardSubject() {
        return tupleKey.getSubject().isWildcard();
    }

    /**
     * 判断是否有关联条件
     *
     * @return 有条件信息返回 true
     */
    public boolean hasCondition() {
        return condition != null && !condition.isEmpty();
    }

    public String getConditionName() {
        return condition != null ? condition.conditionName() : null;
    }

    public String getConditionContext() {
        return condition != null ? condition.conditionContext() : null;
    }
}
