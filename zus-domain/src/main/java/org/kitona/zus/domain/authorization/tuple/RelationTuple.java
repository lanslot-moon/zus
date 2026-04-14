package org.kitona.zus.domain.authorization.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.Objects;

/**
 * 关系元组聚合根。
 *
 * <p>RelationTuple 表示一条已经落地的授权关系，是授权写链路和求值链路共同使用的核心领域对象。
 * 一条 tuple 同时携带主体、资源、关系、zookie、一致性条件和过期时间等完整语义。
 *
 * <p>它的核心业务含义是“某个主体在某种约束下，与某个对象建立了某个关系”。
 * 这里的主体既可以是直接用户，也可以是 userset 或 wildcard，因此该对象需要同时兼容
 * direct subject、userset subject 和 wildcard subject 三种场景。
 */
@EqualsAndHashCode
@ToString
@Getter
public class RelationTuple {

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
     * tuple 条件。
     *
     * <p>真实关联以 {@code conditionDefinitionId} 为准，
     * 其中的条件名只作为写入时的快照字段，便于审计和排查。
     */
    private TupleCondition condition;

    /**
     * 过期时间（毫秒），null 表示永不过期。
     */
    private Long expiresAt;

    /**
     * 是否为通配符主体。
     */
    private boolean wildcard;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 私有构造函数，禁止外部直接 new，保证只能通过 create() 或 reconstitute() 创建
     */
    private RelationTuple() {
    }

    // ========== 工厂方法（业务创建） ==========

    /**
     * 创建关系元组（业务场景）
     *
     * @param storeId  存储空间ID
     * @param tupleKey 元组键
     * @param zookie   Zookie 版本
     * @return RelationTuple 实例
     * @throws NullPointerException 如果 storeId 或 tupleKey 为 null
     */
    public static RelationTuple create(String storeId, TupleKey tupleKey, Zookie zookie) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(tupleKey, "tupleKey 不能为空");

        RelationTuple tuple = new RelationTuple();
        tuple.storeId = storeId;
        tuple.tupleKey = tupleKey;
        tuple.zookie = zookie != null ? zookie : Zookie.EMPTY;
        tuple.condition = TupleCondition.EMPTY;
        tuple.expiresAt = null;
        tuple.wildcard = tupleKey.getSubject().isWildcard();
        tuple.createTime = System.currentTimeMillis();
        return tuple;
    }

    /**
     * 创建带条件信息的关系元组（业务场景）
     *
     * @param storeId             存储空间ID
     * @param tupleKey            元组键
     * @param zookie              Zookie 版本
     * @param conditionDefinitionId 条件定义ID
     * @param conditionName         条件名称快照
     * @param conditionContext      条件上下文
     * @return RelationTuple 实例
     * @throws NullPointerException 如果 storeId 或 tupleKey 为 null
     */
    public static RelationTuple create(String storeId, TupleKey tupleKey, Zookie zookie,
                                       Long conditionDefinitionId, String conditionName, String conditionContext) {
        return create(storeId, tupleKey, zookie,
                TupleCondition.of(conditionDefinitionId, conditionName, conditionContext));
    }

    public static RelationTuple create(String storeId, TupleKey tupleKey, Zookie zookie,
                                       TupleCondition condition) {
        return create(storeId, tupleKey, zookie, condition, null);
    }

    public static RelationTuple create(String storeId, TupleKey tupleKey, Zookie zookie,
                                       TupleCondition condition, Long expiresAt) {
        RelationTuple tuple = create(storeId, tupleKey, zookie);
        tuple.condition = condition != null ? condition : TupleCondition.EMPTY;
        tuple.expiresAt = expiresAt;
        return tuple;
    }

    // ========== 重建方法（持久化恢复） ==========

    /**
     * 从持久化数据重建实体，非业务创建入口。
     *
     * <p>此方法用于从数据库查询结果还原实体状态，不执行业务校验。
     *
     * @param id                  主键ID
     * @param storeId             存储空间ID
     * @param tupleKey            元组键
     * @param zookie              Zookie 版本
     * @param condition           条件信息
     * @param createTime          创建时间
     * @return 重建后的实体
     */
    public static RelationTuple reconstitute(Long id, String storeId, TupleKey tupleKey,
                                             Zookie zookie, Long conditionDefinitionId, String conditionName, String conditionContext,
                                             Long createTime) {
        return reconstitute(id, storeId, tupleKey, zookie,
                TupleCondition.of(conditionDefinitionId, conditionName, conditionContext),
                null, tupleKey != null && tupleKey.getSubject() != null && tupleKey.getSubject().isWildcard(), createTime);
    }

    public static RelationTuple reconstitute(Long id, String storeId, TupleKey tupleKey,
                                             Zookie zookie, TupleCondition condition, Long createTime) {
        return reconstitute(id, storeId, tupleKey, zookie, condition, null,
                tupleKey != null && tupleKey.getSubject() != null && tupleKey.getSubject().isWildcard(), createTime);
    }

    public static RelationTuple reconstitute(Long id, String storeId, TupleKey tupleKey,
                                             Zookie zookie, TupleCondition condition,
                                             Long expiresAt, boolean wildcard, Long createTime) {
        RelationTuple tuple = new RelationTuple();
        tuple.id = id;
        tuple.storeId = storeId;
        tuple.tupleKey = tupleKey;
        tuple.zookie = zookie != null ? zookie : Zookie.EMPTY;
        tuple.condition = condition != null ? condition : TupleCondition.EMPTY;
        tuple.expiresAt = expiresAt;
        tuple.wildcard = wildcard;
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
        return wildcard;
    }

    /**
     * 判断是否有关联条件
     *
     * @return 有条件信息返回 true
     */
    public boolean hasCondition() {
        return condition != null && !condition.isEmpty();
    }

    public Long getConditionDefinitionId() {
        return condition != null ? condition.conditionDefinitionId() : null;
    }

    public String getConditionName() {
        return condition != null ? condition.conditionName() : null;
    }

    public String getConditionContext() {
        return condition != null ? condition.conditionContext() : null;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(long currentTimeMillis) {
        return expiresAt != null && expiresAt <= currentTimeMillis;
    }

    public boolean isWildcard() {
        return wildcard;
    }
}
