package org.kitona.zus.domain.authorization.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 授权模型聚合根。
 *
 * <p>该聚合表示某个 store 下的一整套授权模型定义，是“结构化模型为唯一真相”这条规则的承载者。
 * 聚合内部统一维护类型、关系、限制和条件定义，并在发布时生成 DSL 快照文本。
 *
 * <p>聚合的职责边界很明确：
 * <ul>
 *   <li>保证模型标识、类型名、关系名和条件名在当前聚合内的唯一性</li>
 *   <li>控制模型生命周期，只允许草稿模型被修改或发布</li>
 *   <li>维护模型快照文本，但不把 {@code dslText} 当作写侧真相</li>
 * </ul>
 *
 * <p>持久化恢复可以通过 {@link #reconstitute(AuthorizationModelSnapshot)} 重建聚合，
 * 业务写侧则必须通过工厂方法创建草稿模型，再逐步补齐类型与条件定义。
 */
@EqualsAndHashCode
@ToString
@Getter
public class AuthorizationModelAggregate {

    /**
     * 存储空间ID
     */
    private String storeId;

    /**
     * 模型唯一标识（ULID）
     */
    private String modelId;

    /**
     * Schema 版本，如 1.1
     */
    private String schemaVersion;

    /**
     * 原始 DSL 文本
     */
    private String dslText;

    /**
     * 模型状态
     */
    private ModelPublishStatus status;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 类型定义列表（聚合内实体）
     * <p>
     * 包含模型中所有的类型定义及其关系定义。
     * 通过 {@link #getTypeDefinitions()} 对外返回不可变视图，防止外部修改。
     */
    @Getter(AccessLevel.NONE)
    private List<TypeDefinition> typeDefinitions;

    @Getter(AccessLevel.NONE)
    private List<ConditionDefinition> conditionDefinitions;

    /**
     * 私有构造器，禁止外部直接 new，保证只能通过 create() 或 reconstitute() 创建。
     */
    private AuthorizationModelAggregate() {
        this.schemaVersion = "1.1";
        this.status = ModelPublishStatus.DRAFT;
        this.typeDefinitions = new ArrayList<>();
        this.conditionDefinitions = new ArrayList<>();
    }

    /**
     * 返回类型定义列表的不可变视图（防止调用方修改聚合内部状态）
     */
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions == null ? List.of() : Collections.unmodifiableList(typeDefinitions);
    }

    public List<ConditionDefinition> getConditionDefinitions() {
        return conditionDefinitions == null ? List.of() : Collections.unmodifiableList(conditionDefinitions);
    }

    // ========== 工厂方法 ==========

    /**
     * 创建新的授权模型（草稿状态）
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return AuthorizationModelAggregate 实例
     */
    public static AuthorizationModelAggregate create(String storeId, String modelId) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(modelId, "modelId 不能为空");

        AuthorizationModelAggregate aggregate = new AuthorizationModelAggregate();
        aggregate.storeId = storeId;
        aggregate.modelId = modelId;
        aggregate.createTime = System.currentTimeMillis();
        return aggregate;
    }

    /**
     * 创建新的授权模型，自动生成模型ID（草稿状态）
     *
     * @param storeId 存储空间ID
     * @return AuthorizationModelAggregate 实例
     */
    public static AuthorizationModelAggregate createWithGeneratedId(String storeId) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        String modelId = generateModelId();
        return create(storeId, modelId);
    }

    /**
     * 创建新的授权模型（带 Schema 版本与描述）
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param schemaVersion Schema 版本，可为 null（默认 "1.1"）
     * @param description 描述
     * @return AuthorizationModelAggregate 实例
     */
    public static AuthorizationModelAggregate create(String storeId, String modelId,
                                                     String schemaVersion, String description) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(modelId, "modelId 不能为空");

        AuthorizationModelAggregate aggregate = new AuthorizationModelAggregate();
        aggregate.storeId = storeId;
        aggregate.modelId = modelId;
        aggregate.schemaVersion = schemaVersion != null && !schemaVersion.isEmpty() ? schemaVersion : "1.1";
        aggregate.description = description;
        aggregate.createTime = System.currentTimeMillis();
        return aggregate;
    }

    /**
     * 创建新的授权模型，自动生成模型ID（带 Schema 版本与描述）
     *
     * @param storeId      存储空间ID
     * @param schemaVersion Schema 版本，可为 null（默认 "1.1"）
     * @param description 描述
     * @return AuthorizationModelAggregate 实例
     */
    public static AuthorizationModelAggregate createWithGeneratedId(String storeId, String schemaVersion,
                                                                    String description) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        String modelId = generateModelId();
        return create(storeId, modelId, schemaVersion, description);
    }

    /**
     * 从持久化快照重建聚合根，非业务创建入口。
     * 使用 {@link AuthorizationModelSnapshot} 参数对象，避免方法参数过多。
     * 类型定义需在重建后通过 {@link #reconstituteTypeDefinitions(List)} 再次填充。
     *
     * @param snapshot 持久化快照
     * @return 重建后的聚合根（typeDefinitions 为空列表，由调用方后续设置）
     */
    public static AuthorizationModelAggregate reconstitute(AuthorizationModelSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        AuthorizationModelAggregate aggregate = new AuthorizationModelAggregate();
        aggregate.storeId = snapshot.storeId();
        aggregate.modelId = snapshot.modelId();
        aggregate.schemaVersion = snapshot.schemaVersion() != null && !snapshot.schemaVersion().isEmpty()
                ? snapshot.schemaVersion() : "1.1";
        aggregate.dslText = snapshot.dslText();
        aggregate.status = snapshot.status() != null ? snapshot.status() : ModelPublishStatus.DRAFT;
        aggregate.description = snapshot.description();
        aggregate.createTime = snapshot.createTime();
        aggregate.typeDefinitions = new ArrayList<>();
        aggregate.conditionDefinitions = new ArrayList<>();
        return aggregate;
    }

    // ========== 状态查询方法 ==========

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return status == ModelPublishStatus.DRAFT;
    }

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return status == ModelPublishStatus.PUBLISHED;
    }

    /**
     * 是否已废弃
     */
    public boolean isDeprecated() {
        return status == ModelPublishStatus.ABANDONED;
    }

    /**
     * 是否可编辑（只有草稿状态可以编辑）
     */
    public boolean isEditable() {
        return isDraft();
    }

    /**
     * 获取状态值（向后兼容）
     *
     * @return 状态数值
     */
    public Integer getStatusValue() {
        return status != null ? status.getStatus() : null;
    }

    // ========== 类型定义管理方法 ==========

    /**
     * 添加类型定义
     *
     * @param typeDefinition 类型定义实体
     * @throws IllegalStateException    如果模型不是草稿状态
     * @throws IllegalArgumentException 如果类型已存在
     */
    public void addTypeDefinition(TypeDefinition typeDefinition) {
        assertEditable();
        Objects.requireNonNull(typeDefinition, "类型定义不能为空");

        // 检查类型是否重复
        boolean exists = typeDefinitions.stream()
                .anyMatch(t -> t.getSubjectType().equals(typeDefinition.getSubjectType()));
        if (exists) {
            throw new SystemException("类型已存在: " + typeDefinition.getSubjectType(), IError.DATA_EXIST_ERROR.getCode());
        }

        // 设置排序序号
        if (typeDefinition.getSortOrder() == null || typeDefinition.getSortOrder() == 0) {
            typeDefinition.assignSortOrder(typeDefinitions.size());
        }

        this.typeDefinitions.add(typeDefinition);
    }

    /**
     * 批量添加类型定义
     *
     * @param definitions 类型定义列表
     */
    public void addTypeDefinitions(List<TypeDefinition> definitions) {
        if (definitions != null) {
            definitions.forEach(this::addTypeDefinition);
        }
    }

    /**
     * 获取指定类型的定义
     *
     * @param type 类型名称
     * @return 类型定义，不存在返回 empty
     */
    public Optional<TypeDefinition> getTypeDefinition(String type) {
        return typeDefinitions.stream()
                .filter(t -> t.getSubjectType().equals(type))
                .findFirst();
    }

    /**
     * 判断是否包含指定类型
     *
     * @param type 类型名称
     * @return 包含返回 true
     */
    public boolean hasTypeDefinition(String type) {
        return typeDefinitions.stream()
                .anyMatch(t -> t.getSubjectType().equals(type));
    }

    /**
     * 移除类型定义
     *
     * @param type 类型名称
     * @return 被移除的类型定义，不存在返回 empty
     * @throws IllegalStateException 如果模型不是草稿状态
     */
    public Optional<TypeDefinition> removeTypeDefinition(String type) {
        assertEditable();
        Optional<TypeDefinition> toRemove = getTypeDefinition(type);
        toRemove.ifPresent(typeDefinitions::remove);
        return toRemove;
    }

    /**
     * 获取所有类型名称
     *
     * @return 类型名称集合
     */
    public Set<String> getTypeNames() {
        return typeDefinitions.stream()
                .map(TypeDefinition::getSubjectType)
                .collect(Collectors.toSet());
    }

    /**
     * 获取类型数量
     *
     * @return 类型数量
     */
    public int getTypeCount() {
        return typeDefinitions.size();
    }

    /**
     * 判断是否有类型定义
     *
     * @return 有类型定义返回 true
     */
    public boolean hasTypeDefinitions() {
        return !typeDefinitions.isEmpty();
    }

    public void addConditionDefinition(ConditionDefinition conditionDefinition) {
        assertEditable();
        Objects.requireNonNull(conditionDefinition, "conditionDefinition 不能为空");
        boolean exists = conditionDefinitions.stream()
                .anyMatch(item -> item.getConditionName().equals(conditionDefinition.getConditionName()));
        if (exists) {
            throw new SystemException("条件已存在: " + conditionDefinition.getConditionName(), IError.DATA_EXIST_ERROR.getCode());
        }
        conditionDefinitions.add(conditionDefinition);
    }

    public void replaceConditionDefinitions(List<ConditionDefinition> definitions) {
        assertEditable();
        conditionDefinitions.clear();
        if (definitions != null) {
            conditionDefinitions.addAll(definitions);
        }
    }

    public Optional<ConditionDefinition> getConditionDefinition(String conditionName) {
        return conditionDefinitions.stream()
                .filter(item -> item.getConditionName().equals(conditionName))
                .findFirst();
    }

    // ========== 关系定义管理方法 ==========

    /**
     * 向指定类型添加关系定义
     *
     * @param type       类型名称
     * @param definition 关系定义
     * @throws IllegalStateException    如果模型不是草稿状态
     * @throws IllegalArgumentException 如果类型不存在
     */
    public void addRelationToType(String type, RelationDefinition definition) {
        assertEditable();
        TypeDefinition typeEntity = getTypeDefinition(type)
                .orElseThrow(() -> new SystemException("类型不存在: " + type, IError.DATA_NOT_EXIST.getCode()));
        typeEntity.addRelation(definition);
    }

    /**
     * 获取指定类型的指定关系定义
     *
     * @param type         类型名称
     * @param relationName 关系名称
     * @return 关系定义，不存在返回 empty
     */
    public Optional<RelationDefinition> getRelation(String type, String relationName) {
        return getTypeDefinition(type)
                .map(t -> t.getRelation(relationName));
    }

    /**
     * 判断指定类型是否包含指定关系
     *
     * @param type         类型名称
     * @param relationName 关系名称
     * @return 包含返回 true
     */
    public boolean hasRelation(String type, String relationName) {
        return getTypeDefinition(type)
                .map(t -> t.hasRelation(relationName))
                .orElse(false);
    }

    /**
     * 更新指定类型下的关系定义（新增或覆盖）
     *
     * @param type       类型名称
     * @param definition 关系定义
     * @throws IllegalStateException    如果模型不是草稿状态
     * @throws IllegalArgumentException 如果类型不存在
     */
    public void updateRelationInType(String type, RelationDefinition definition) {
        assertEditable();
        TypeDefinition typeEntity = getTypeDefinition(type)
                .orElseThrow(() -> new SystemException("类型不存在: " + type, IError.DATA_NOT_EXIST.getCode()));
        typeEntity.putRelation(definition);
    }

    /**
     * 移除指定类型下的关系定义
     *
     * @param type         类型名称
     * @param relationName 关系名称
     * @return 被移除的关系定义，不存在返回 empty
     * @throws IllegalStateException 如果模型不是草稿状态
     */
    public Optional<RelationDefinition> removeRelationFromType(String type, String relationName) {
        assertEditable();
        Optional<TypeDefinition> typeOpt = getTypeDefinition(type);
        if (typeOpt.isEmpty()) {
            return Optional.empty();
        }
        RelationDefinition removed = typeOpt.get().removeRelation(relationName);
        return Optional.ofNullable(removed);
    }

    // ========== 状态变更方法 ==========

    /**
     * 发布模型
     *
     * <p>将模型从草稿状态变更为已发布状态。
     * 发布后模型不可修改，成为只读状态。
     *
     * @throws IllegalStateException 如果不是草稿状态或没有类型定义
     */
    public void publish(String dslSnapshot) {
        if (!isDraft()) {
            throw new SystemException("只有草稿状态的模型可以发布，当前状态: " + status, IError.DATA_STATUS_ERROR.getCode());
        }
        if (typeDefinitions.isEmpty()) {
            throw new SystemException("模型至少需要一个类型定义才能发布", IError.PARAMS_EXIST_ERROR.getCode());
        }
        this.dslText = dslSnapshot;
        this.status = ModelPublishStatus.PUBLISHED;
    }

    /**
     * 废弃模型
     *
     * <p>将模型从已发布状态变更为已废弃状态。
     * 废弃后模型仍可用于历史查询，但不会被用于新的权限检查。
     *
     * @throws IllegalStateException 如果不是已发布状态
     */
    public void deprecate() {
        if (!isPublished()) {
            throw new SystemException("只有已发布的模型可以废弃，当前状态: " + status, IError.DATA_STATUS_ERROR.getCode());
        }
        this.status = ModelPublishStatus.ABANDONED;
    }

    /**
     * 更新描述
     *
     * @param description 新的描述
     */
    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * 清空并重新设置类型定义（仅草稿状态）
     *
     * @param newTypeDefinitions 新的类型定义列表
     * @throws IllegalStateException 如果不是草稿状态
     */
    public void replaceTypeDefinitions(List<TypeDefinition> newTypeDefinitions) {
        assertEditable();
        this.typeDefinitions.clear();
        if (newTypeDefinitions != null) {
            for (int i = 0; i < newTypeDefinitions.size(); i++) {
                TypeDefinition typeDef = newTypeDefinitions.get(i);
                typeDef.assignSortOrder(i);
                this.typeDefinitions.add(typeDef);
            }
        }
    }

    // ========== 内部方法 ==========

    /**
     * 断言模型处于可编辑状态
     *
     * @throws IllegalStateException 如果不是草稿状态
     */
    private void assertEditable() {
        if (!isEditable()) {
            throw new SystemException("模型不可编辑，只有草稿状态可以修改，当前状态: " + status, IError.DATA_STATUS_ERROR.getCode());
        }
    }

    /**
     * 从持久化恢复流程中补齐类型定义，非业务 API。
     *
     * @param typeDefinitions 类型定义列表
     */
    public void reconstituteTypeDefinitions(List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions != null ? new ArrayList<>(typeDefinitions) : new ArrayList<>();
    }

    public void reconstituteConditionDefinitions(List<ConditionDefinition> conditionDefinitions) {
        this.conditionDefinitions = conditionDefinitions != null ? new ArrayList<>(conditionDefinitions) : new ArrayList<>();
    }

    // ========== ID 生成方法 ==========

    /**
     * 生成模型ID（领域层 ID 生成策略）
     *
     * <p>格式：model-{uuid32}，如 model-a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
     *
     * @return 模型ID
     */
    private static String generateModelId() {
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
        return "model-" + uuid;
    }
}
