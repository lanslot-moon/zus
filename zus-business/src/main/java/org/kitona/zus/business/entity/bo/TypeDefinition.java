package org.kitona.zus.business.entity.bo;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


/**
 * 使用 Java record 类型定义的不可变数据类型，表示资源类型定义及其关系映射
 * record 提供了简洁的语法来创建不可变数据载体
 */
public record TypeDefinition(String resourceType, Map<String, RelationDefinition> relations, // 定义资源类型和关系映射
                             Map<String, Set<String>> relationRestrictions) { // 定义关系限制映射

    /**
     * 全参数构造函数的紧凑形式
     * 在这里进行参数校验和初始化操作
     */
    public TypeDefinition {
        // 确保 relationRestrictions 不为 null
        if (relationRestrictions == null) { // 检查 relationRestrictions 是否为 null
            relationRestrictions = Collections.emptyMap(); // 如果为 null，则初始化为空 Map
        }
    }

    // 简化构造函数，兼容旧代码
    public TypeDefinition(String resourceType, Map<String, RelationDefinition> relations) { // 提供简化构造函数，不指定 relationRestrictions
        this(resourceType, relations, Collections.emptyMap()); // 调用完整构造函数，使用空 Map 作为 relationRestrictions
    }

    /**
     * 辅助方法：获取特定关系上的 Type Restrictions
     *
     * @param relationName 关系名称
     * @return 返回与该关系关联的类型限制集合，如果不存在则返回空集合
     */
    public Set<String> getRestrictionsForRelation(String relationName) { // 根据关系名称获取类型限制
        return relationRestrictions.getOrDefault(relationName, Set.of()); // 返回指定关系的限制集合，如果不存在则返回空集合
    }
}