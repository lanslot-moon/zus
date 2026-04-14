package org.kitona.zus.domain.authorization.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 关系定义值对象（Value Object）
 *
 * <p>描述对象类型中单个 relation（如 viewer）的访问逻辑。
 * 作为值对象，它是不可变的，通过所有属性来判断相等性。
 *
 * <p>关系定义包含三个核心部分：
 * <ul>
 *   <li>relationName: 关系名称，如 viewer、editor、owner</li>
 *   <li>rewriteExpression: 重写表达式，定义关系的计算规则</li>
 *   <li>restrictions: 类型限制，定义哪些主体类型可以拥有该关系</li>
 * </ul>
 *
 * <p>重写表达式类型：
 * <ul>
 *   <li>self: 直接关系，通过元组直接赋予</li>
 *   <li>self or X: 计算关系，viewer 可以是直接赋予或从 X 关系继承</li>
 *   <li>X from Y: TTU（Tuple-to-Userset），通过 Y 关系引用的对象的 X 关系</li>
 * </ul>
 *
 * <p>类型限制示例：
 * <ul>
 *   <li>[user]: 只允许 user 类型</li>
 *   <li>[user, group#member]: 允许 user 或 group 的 member</li>
 *   <li>[user:*]: 允许所有 user（通配符）</li>
 * </ul>
 *
 * @param relationName      关系名，例如 viewer、editor、owner
 * @param rewriteExpression 关系定义表达式，描述访问规则
 * @param restrictions      类型限制集合，定义允许的主体类型
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record RelationDefinition(String relationName, String rewriteExpression, Set<String> restrictions) {

    /**
     * 规范化构造函数，确保 restrictions 不为 null
     */
    public RelationDefinition {
        if (restrictions == null) {
            restrictions = Collections.emptySet();
        } else {
            // 创建不可变副本
            restrictions = Set.copyOf(restrictions);
        }
    }

    /**
     * 简化构造函数（无类型限制）
     *
     * @param relationName      关系名
     * @param rewriteExpression 重写表达式
     */
    public RelationDefinition(String relationName, String rewriteExpression) {
        this(relationName, rewriteExpression, Collections.emptySet());
    }

    // ========== 工厂方法 ==========

    /**
     * 创建直接关系定义（self）
     *
     * @param relationName 关系名
     * @param restrictions 类型限制
     * @return RelationDefinition 实例
     */
    public static RelationDefinition direct(String relationName, Set<String> restrictions) {
        return new RelationDefinition(relationName, "self", restrictions);
    }

    /**
     * 创建直接关系定义（self，单一类型限制）
     *
     * @param relationName    关系名
     * @param allowedType     允许的类型
     * @return RelationDefinition 实例
     */
    public static RelationDefinition direct(String relationName, String allowedType) {
        return new RelationDefinition(relationName, "self", Set.of(allowedType));
    }

    /**
     * 创建计算关系定义
     *
     * @param relationName      关系名
     * @param rewriteExpression 重写表达式
     * @param restrictions      类型限制
     * @return RelationDefinition 实例
     */
    public static RelationDefinition computed(String relationName, String rewriteExpression, Set<String> restrictions) {
        return new RelationDefinition(relationName, rewriteExpression, restrictions);
    }

    // ========== 查询方法 ==========
    public Set<String> getRestrictions() {
        return restrictions;
    }

    /**
     * 判断是否为直接关系（self）
     *
     * @return 是直接关系返回 true
     */
    public boolean isDirectRelation() {
        return "self".equals(rewriteExpression);
    }

    /**
     * 判断是否为计算关系（包含 or、and 等运算符）
     *
     * @return 是计算关系返回 true
     */
    public boolean isComputedRelation() {
        return rewriteExpression != null &&
                (rewriteExpression.contains(" or ") ||
                        rewriteExpression.contains(" and ") ||
                        rewriteExpression.contains(" but not "));
    }

    /**
     * 判断是否为 TTU 关系（包含 from 关键字）
     *
     * @return 是 TTU 关系返回 true
     */
    public boolean isTtuRelation() {
        return rewriteExpression != null && rewriteExpression.contains(" from ");
    }

    /**
     * 判断是否允许指定主体类型
     *
     * @param subjectType 主体类型
     * @return 允许返回 true
     */
    public boolean allowsSubjectType(String subjectType) {
        if (restrictions.isEmpty()) {
            return true; // 无限制时允许所有类型
        }
        // 直接匹配或 userset 匹配（如 group#member 匹配 group）
        return restrictions.contains(subjectType) ||
                restrictions.stream().anyMatch(r -> r.startsWith(subjectType + "#"));
    }

    /**
     * 判断是否有类型限制
     *
     * @return 有限制返回 true
     */
    public boolean hasRestrictions() {
        return !restrictions.isEmpty();
    }

    /**
     * 添加类型限制（返回新实例，保持不可变性）
     *
     * @param allowedType 允许的类型
     * @return 新的 RelationDefinition 实例
     */
    public RelationDefinition withRestriction(String allowedType) {
        Set<String> newRestrictions = new HashSet<>(this.restrictions);
        newRestrictions.add(allowedType);
        return new RelationDefinition(this.relationName, this.rewriteExpression, newRestrictions);
    }

    /**
     * 添加多个类型限制（返回新实例，保持不可变性）
     *
     * @param allowedTypes 允许的类型集合
     * @return 新的 RelationDefinition 实例
     */
    public RelationDefinition withRestrictions(Set<String> allowedTypes) {
        Set<String> newRestrictions = new HashSet<>(this.restrictions);
        newRestrictions.addAll(allowedTypes);
        return new RelationDefinition(this.relationName, this.rewriteExpression, newRestrictions);
    }

    @Override
    public String toString() {
        return "RelationDefinition{" +
                "name='" + relationName + '\'' +
                ", expression='" + rewriteExpression + '\'' +
                ", restrictions=" + restrictions +
                '}';
    }
}
