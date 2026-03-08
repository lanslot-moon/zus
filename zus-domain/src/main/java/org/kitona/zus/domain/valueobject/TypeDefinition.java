package org.kitona.zus.domain.valueobject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 类型定义值对象（Value Object）
 * <p>
 * 表示资源类型定义及其关系映射。作为值对象，它是不可变的。
 *
 * @param resourceType         资源类型，例如 document
 * @param relations            资源类型和关系映射,例如 viewer, [viewer self or writer or viewer from parentFolder]
 * @param relationRestrictions 关系限制映射（Type Restrictions）
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record TypeDefinition(String resourceType, Map<String, RelationDefinition> relations,
                             Map<String, Set<String>> relationRestrictions) {

    public TypeDefinition {
        if (relationRestrictions == null) {
            relationRestrictions = Collections.emptyMap();
        }
    }

    /**
     * 简化构造函数，不指定 relationRestrictions 时使用空 Map
     */
    public TypeDefinition(String resourceType, Map<String, RelationDefinition> relations) {
        this(resourceType, relations, Collections.emptyMap());
    }

    /**
     * 获取特定关系上的 Type Restrictions
     *
     * @param relationName 关系名称
     * @return 与该关系关联的类型限制集合，不存在则返回空集合
     */
    public Set<String> getRestrictionsForRelation(String relationName) {
        return relationRestrictions.getOrDefault(relationName, Set.of());
    }
}
