package org.kitona.zus.domain.authorization.model;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 授权模型结构值对象（聚合内「体」）。
 *
 * <p>按 ReBAC 自然键（type 名、relation 名、condition 名）组织，与
 * {@code fga_type_definition} / {@code fga_relation_definition} / {@code fga_condition_definition}
 * 唯一约束对齐，便于持久化层 name-keyed diff。
 */
public record AuthorizationModelStructure(Map<String, TypeDefinition> typesByName,
                                          Map<String, ConditionDefinition> conditionsByName) {

    /**
     * 创建不可变结构快照。
     *
     * @param typesByName      按 type 名称索引的类型定义
     * @param conditionsByName 按 condition 名称索引的条件定义
     */
    public AuthorizationModelStructure {
        typesByName = immutableCopy(typesByName);
        conditionsByName = immutableCopy(conditionsByName);
    }

    /**
     * 空结构。
     */
    public static AuthorizationModelStructure empty() {
        return new AuthorizationModelStructure(Map.of(), Map.of());
    }


    /**
     * 按 sort_order 排序的类型列表（只读）。
     */
    public List<TypeDefinition> orderedTypes() {
        return typesByName.values().stream()
                .sorted((left, right) -> {
                    int leftOrder = Optional.ofNullable(left.getSortOrder()).orElse(0);
                    int rightOrder = Optional.ofNullable(right.getSortOrder()).orElse(0);
                    int cmp = Integer.compare(leftOrder, rightOrder);
                    if (cmp != 0) {
                        return cmp;
                    }
                    return left.getSubjectType().compareTo(right.getSubjectType());
                })
                .toList();
    }

    /**
     * 条件定义列表（只读）。
     */
    public List<ConditionDefinition> conditions() {
        return List.copyOf(conditionsByName.values());
    }

    /**
     * 从类型与条件列表构建结构（应用层装配、持久化重建入口）。
     */
    public static AuthorizationModelStructure fromTypesAndConditions(List<TypeDefinition> types,
                                                                     List<ConditionDefinition> conditions) {
        // 主方法逻辑清晰：转换类型、转换条件、组装结果
        Map<String, TypeDefinition> typeMap = convertTypes(types);
        Map<String, ConditionDefinition> conditionMap = convertConditions(conditions);

        return new AuthorizationModelStructure(typeMap, conditionMap);
    }

    /**
     * 转换并校验类型定义列表
     */
    private static Map<String, TypeDefinition> convertTypes(List<TypeDefinition> types) {
        Map<String, TypeDefinition> typeMap = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(types)) {
            return typeMap;
        }

        for (int i = 0; i < types.size(); i++) {
            TypeDefinition type = types.get(i);
            validateAndAssignType(type, i, typeMap);
        }
        return typeMap;
    }

    /**
     * 校验类型定义并分配排序号
     */
    private static void validateAndAssignType(TypeDefinition type, int index, Map<String, TypeDefinition> typeMap) {
        Objects.requireNonNull(type, "类型定义不能为空");
        String name = type.getSubjectType();

        // 使用 putIfAbsent 替换 containsKey，降低复杂度并保证原子性
        TypeDefinition existingType = typeMap.putIfAbsent(name, type);
        if (existingType != null) {
            throw new SystemException("类型已存在: " + name, IError.DATA_EXIST_ERROR.getCode());
        }

        if (type.getSortOrder() == null || type.getSortOrder() == 0) {
            type.assignSortOrder(index);
        }
    }

    /**
     * 转换并校验条件定义列表
     */
    private static Map<String, ConditionDefinition> convertConditions(List<ConditionDefinition> conditions) {
        Map<String, ConditionDefinition> conditionMap = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(conditions)) {
            return conditionMap;
        }

        for (ConditionDefinition condition : conditions) {
            Objects.requireNonNull(condition, "条件定义不能为空");
            String name = condition.getConditionName();
            ConditionDefinition existingCondition = conditionMap.putIfAbsent(name, condition);
            if (existingCondition != null) {
                throw new SystemException("条件已存在: " + name, IError.DATA_EXIST_ERROR.getCode());
            }
        }
        return conditionMap;
    }


    public Optional<TypeDefinition> getType(String typeName) {
        return Optional.ofNullable(typesByName.get(typeName));
    }

    public boolean hasType(String typeName) {
        return typesByName.containsKey(typeName);
    }

    public int typeCount() {
        return typesByName.size();
    }

    public boolean hasTypes() {
        return !typesByName.isEmpty();
    }

    public Set<String> typeNames() {
        return Set.copyOf(typesByName.keySet());
    }

    public Optional<ConditionDefinition> getCondition(String conditionName) {
        return Optional.ofNullable(conditionsByName.get(conditionName));
    }

    public boolean hasCondition(String conditionName) {
        return conditionsByName.containsKey(conditionName);
    }

    /**
     * 返回添加类型后的新结构。
     *
     * @param typeDefinition 类型定义
     * @return 添加类型后的新结构
     */
    public AuthorizationModelStructure withType(TypeDefinition typeDefinition) {
        Objects.requireNonNull(typeDefinition, "类型定义不能为空");
        String name = typeDefinition.getSubjectType();
        if (typesByName.containsKey(name)) {
            throw new SystemException("类型已存在: " + name, IError.DATA_EXIST_ERROR.getCode());
        }
        if (typeDefinition.getSortOrder() == null || typeDefinition.getSortOrder() == 0) {
            typeDefinition.assignSortOrder(typesByName.size());
        }
        Map<String, TypeDefinition> nextTypes = new LinkedHashMap<>(typesByName);
        nextTypes.put(name, typeDefinition);
        return new AuthorizationModelStructure(nextTypes, conditionsByName);
    }

    /**
     * 返回移除类型后的新结构。
     *
     * @param typeName 类型名称
     * @return 移除类型后的新结构
     */
    public AuthorizationModelStructure withoutType(String typeName) {
        if (!typesByName.containsKey(typeName)) {
            return this;
        }
        Map<String, TypeDefinition> nextTypes = new LinkedHashMap<>(typesByName);
        nextTypes.remove(typeName);
        return new AuthorizationModelStructure(nextTypes, conditionsByName);
    }

    /**
     * 返回添加条件后的新结构。
     *
     * @param conditionDefinition 条件定义
     * @return 添加条件后的新结构
     */
    public AuthorizationModelStructure withCondition(ConditionDefinition conditionDefinition) {
        Objects.requireNonNull(conditionDefinition, "conditionDefinition 不能为空");
        String name = conditionDefinition.getConditionName();
        if (conditionsByName.containsKey(name)) {
            throw new SystemException("条件已存在: " + name, IError.DATA_EXIST_ERROR.getCode());
        }
        Map<String, ConditionDefinition> nextConditions = new LinkedHashMap<>(conditionsByName);
        nextConditions.put(name, conditionDefinition);
        return new AuthorizationModelStructure(typesByName, nextConditions);
    }

    /**
     * 返回替换全部条件后的新结构。
     *
     * @param definitions 条件定义列表
     * @return 替换全部条件后的新结构
     */
    public AuthorizationModelStructure withConditions(List<ConditionDefinition> definitions) {
        return new AuthorizationModelStructure(typesByName, convertConditions(definitions));
    }

    private static <T> Map<String, T> immutableCopy(Map<String, T> source) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }
}
