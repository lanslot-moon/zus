package org.kitona.zus.domain.authorization.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 类型定义实体。
 *
 * <p>这是 {@link AuthorizationModelAggregate} 聚合内部的子实体，用来描述某一种资源类型
 * 以及它拥有的关系集合。它本身不是聚合根，任何状态变更都应由模型聚合统一约束。
 *
 * <p>一个类型定义通常对应 DSL 里的一个 {@code type} 段，例如 document、folder、group。
 * 该实体负责保证同一类型下的关系名唯一，并维护关系定义的自然顺序。
 */
@Getter
@ToString
@EqualsAndHashCode
public class TypeDefinition {

    /**
     * 主键ID（数据库）
     */
    private Long id;

    /**
     * 类型名称，如 document、folder、user
     */
    private String subjectType;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 关系定义映射：relationName -> RelationDefinition
     * <p>
     * 例如：{"owner" -> RelationDefinition(...), "viewer" -> RelationDefinition(...)}
     * 通过 {@link #getRelations()} 对外返回不可变视图，防止外部修改。
     */
    @Getter(AccessLevel.NONE)
    private Map<String, RelationDefinition> relations = new HashMap<>();

    /**
     * 返回关系定义的只读视图。
     */
    public Map<String, RelationDefinition> getRelations() {
        return relations == null ? Map.of() : Collections.unmodifiableMap(relations);
    }

    /**
     * 私有构造函数，强制调用方通过工厂方法创建或重建实体。
     */
    private TypeDefinition() {
        this.sortOrder = 0;
    }

    // ========== 工厂方法（业务创建） ==========

    /**
     * 创建类型定义实体（业务场景）
     *
     * @param subjectType 类型名称
     * @return TypeDefinition 实例
     * @throws NullPointerException 如果 type 为 null
     */
    public static TypeDefinition create(String subjectType) {
        Objects.requireNonNull(subjectType, "类型名称不能为空");
        TypeDefinition entity = new TypeDefinition();
        entity.subjectType = subjectType;
        entity.sortOrder = 0;
        return entity;
    }

    /**
     * 创建类型定义实体（业务场景，带排序）
     *
     * @param subjectType      类型名称
     * @param sortOrder 排序序号
     * @return TypeDefinition 实例
     * @throws NullPointerException 如果 type 为 null
     */
    public static TypeDefinition create(String subjectType, Integer sortOrder) {
        Objects.requireNonNull(subjectType, "类型名称不能为空");
        TypeDefinition entity = new TypeDefinition();
        entity.subjectType = subjectType;
        entity.sortOrder = sortOrder != null ? sortOrder : 0;
        return entity;
    }

    /**
     * 创建类型定义实体并填充关系定义（业务场景）
     *
     * <p>此工厂方法将「类型名 + 关系定义列表」一次性组装为完整的类型定义实体，
     * 符合 DDD 原则：领域实体通过自身工厂方法创建，而非由外部 Assembler 直接构建。
     *
     * @param subjectType 类型名称
     * @param relationDefinitions 关系定义列表
     * @return 填充好关系定义的 TypeDefinition
     * @throws NullPointerException 如果 subjectType 为 null
     */
    public static TypeDefinition createWithRelations(String subjectType,
                                                     List<RelationDefinition> relationDefinitions) {
        TypeDefinition entity = create(subjectType);
        if (relationDefinitions != null) {
            for (RelationDefinition definition : relationDefinitions) {
                entity.addRelation(definition);
            }
        }
        return entity;
    }

    // ========== 重建方法（持久化恢复） ==========

    /**
     * 从持久化快照重建实体，非业务创建入口。
     *
     * <p>此方法用于从数据库查询结果还原实体状态，不执行业务校验。
     * 关系定义需在重建后通过 {@link #putRelation(RelationDefinition)} 填充。
     *
     * @param id        主键ID
     * @param subjectType      类型名称
     * @param sortOrder 排序序号
     * @return 重建后的实体（relations 为空，由调用方后续填充）
     */
    public static TypeDefinition reconstitute(Long id, String subjectType, Integer sortOrder) {
        TypeDefinition entity = new TypeDefinition();
        entity.id = id;
        entity.subjectType = subjectType;
        entity.sortOrder = sortOrder != null ? sortOrder : 0;
        return entity;
    }

    // ========== 领域行为方法 ==========

    /**
     * 添加关系定义
     *
     * @param definition 关系定义
     * @throws IllegalArgumentException 如果关系名称已存在
     */
    public void addRelation(RelationDefinition definition) {
        Objects.requireNonNull(definition, "关系定义不能为空");
        String relationName = definition.relationName();
        if (relations.containsKey(relationName)) {
            throw new SystemException("关系已存在: " + relationName, IError.DATA_EXIST_ERROR.getCode());
        }
        this.relations.put(relationName, definition);
    }

    /**
     * 添加或更新关系定义
     *
     * @param definition 关系定义
     */
    public void putRelation(RelationDefinition definition) {
        Objects.requireNonNull(definition, "关系定义不能为空");
        this.relations.put(definition.relationName(), definition);
    }

    /**
     * 移除关系定义
     *
     * @param relationName 关系名称
     * @return 被移除的关系定义，不存在返回 null
     */
    public RelationDefinition removeRelation(String relationName) {
        return this.relations.remove(relationName);
    }

    /**
     * 获取指定关系的定义
     *
     * @param relationName 关系名称
     * @return 关系定义，不存在返回 null
     */
    public RelationDefinition getRelation(String relationName) {
        return relations.get(relationName);
    }

    /**
     * 判断是否包含指定关系
     *
     * @param relationName 关系名称
     * @return 包含返回 true
     */
    public boolean hasRelation(String relationName) {
        return relations.containsKey(relationName);
    }

    /**
     * 获取关系的类型限制
     *
     * @param relationName 关系名称
     * @return 类型限制集合，关系不存在返回空集合
     */
    public Set<String> getRestrictionsForRelation(String relationName) {
        RelationDefinition def = relations.get(relationName);
        return def != null ? def.restrictions() : Set.of();
    }

    /**
     * 获取所有关系名称
     *
     * @return 关系名称集合
     */
    public Set<String> getRelationNames() {
        return relations.keySet();
    }

    /**
     * 获取关系数量
     *
     * @return 关系数量
     */
    public int getRelationCount() {
        return relations.size();
    }

    /**
     * 判断是否有关系定义
     *
     * @return 有关系返回 true
     */
    public boolean hasRelations() {
        return !relations.isEmpty();
    }

    /**
     * 分配排序序号（由聚合根在添加/替换类型定义时调用）
     *
     * @param sortOrder 排序序号
     */
    public void assignSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder != null ? sortOrder : 0;
    }
}
