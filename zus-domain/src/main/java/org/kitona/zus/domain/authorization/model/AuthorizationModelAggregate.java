package org.kitona.zus.domain.authorization.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.domain.enums.ModelPublishStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 授权模型聚合根。
 *
 * <p>该聚合表示某个 store 下的一整套授权模型定义，是“结构化模型为唯一真相”这条规则的承载者。
 * 生命周期字段（status、dslText 等）与结构体 {@link AuthorizationModelStructure} 同属本聚合根。
 *
 * <p>聚合的职责边界：
 * <ul>
 *   <li>保证模型标识、类型名、关系名和条件名在当前聚合内的唯一性</li>
 *   <li>控制模型生命周期，只允许草稿模型被修改或发布</li>
 *   <li>维护模型快照文本，但不把 {@code dslText} 当作写侧真相</li>
 * </ul>
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
     * 聚合内结构体：type / relation / condition，按名称索引。
     */
    private AuthorizationModelStructure structure;

    private AuthorizationModelAggregate() {
        this.schemaVersion = "1.1";
        this.status = ModelPublishStatus.DRAFT;
        this.structure = AuthorizationModelStructure.empty();
    }

    /**
     * 类型定义列表（按 sort_order 排序的兼容视图）。
     */
    public List<TypeDefinition> getTypeDefinitions() {
        return structure.orderedTypes();
    }

    public List<ConditionDefinition> getConditionDefinitions() {
        return structure.conditions();
    }

    public static AuthorizationModelAggregate create(String storeId, String modelId) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(modelId, "modelId 不能为空");

        AuthorizationModelAggregate aggregate = new AuthorizationModelAggregate();
        aggregate.storeId = storeId;
        aggregate.modelId = modelId;
        aggregate.createTime = System.currentTimeMillis();
        return aggregate;
    }

    public static AuthorizationModelAggregate createWithGeneratedId(String storeId) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        return create(storeId, generateModelId());
    }

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

    public static AuthorizationModelAggregate createWithGeneratedId(String storeId, String schemaVersion,
                                                                    String description) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        return create(storeId, generateModelId(), schemaVersion, description);
    }

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
        aggregate.structure = AuthorizationModelStructure.empty();
        return aggregate;
    }


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


    /**
     * 添加类型定义
     *
     * @param typeDefinition 类型定义实体
     * @throws IllegalStateException    如果模型不是草稿状态
     * @throws IllegalArgumentException 如果类型已存在
     */
    public void addTypeDefinition(TypeDefinition typeDefinition) {
        assertEditable();
        structure.putType(typeDefinition);
    }

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
        return structure.getType(type);
    }

    /**
     * 判断是否包含指定类型
     *
     * @param type 类型名称
     * @return 包含返回 true
     */
    public boolean hasTypeDefinition(String type) {
        return structure.hasType(type);
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
        return structure.removeType(type);
    }

    /**
     * 获取所有类型名称
     *
     * @return 类型名称集合
     */
    public Set<String> getTypeNames() {
        return structure.typeNames();
    }

    /**
     * 获取类型数量
     *
     * @return 类型数量
     */
    public int getTypeCount() {
        return structure.typeCount();
    }

    /**
     * 判断是否有类型定义
     *
     * @return 有类型定义返回 true
     */
    public boolean hasTypeDefinitions() {
        return structure.hasTypes();
    }

    public void addConditionDefinition(ConditionDefinition conditionDefinition) {
        assertEditable();
        structure.putCondition(conditionDefinition);
    }

    public void replaceConditionDefinitions(List<ConditionDefinition> definitions) {
        assertEditable();
        structure.replaceConditions(definitions);
    }

    public Optional<ConditionDefinition> getConditionDefinition(String conditionName) {
        return structure.getCondition(conditionName);
    }

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
        TypeDefinition typeEntity = getTypeDefinition(type).orElseThrow(() -> new SystemException("类型不存在: " + type, IError.DATA_NOT_EXIST.getCode()));
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
        return getTypeDefinition(type).map(t -> t.getRelation(relationName));
    }

    /**
     * 判断指定类型是否包含指定关系
     *
     * @param type         类型名称
     * @param relationName 关系名称
     * @return 包含返回 true
     */
    public boolean hasRelation(String type, String relationName) {
        return getTypeDefinition(type).map(t -> t.hasRelation(relationName)).orElse(false);
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
        if (!structure.hasTypes()) {
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
     * 全量替换结构体（仅草稿态）。
     */
    public void replaceStructure(AuthorizationModelStructure newStructure) {
        assertEditable();
        this.structure = newStructure != null ? newStructure : AuthorizationModelStructure.empty();
    }


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
     * 从持久化恢复流程补齐结构，非业务 API。
     */
    public void reconstituteStructure(AuthorizationModelStructure modelStructure) {
        this.structure = modelStructure != null ? modelStructure : AuthorizationModelStructure.empty();
    }


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
