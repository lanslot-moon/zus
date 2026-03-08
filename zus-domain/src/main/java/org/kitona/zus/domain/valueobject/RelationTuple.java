package org.kitona.zus.domain.valueobject;

/**
 * 关系元组值对象（Value Object）
 * <p>
 * 表示一个具体的权限关系数据实例。例如 "user:alice is viewer of folder:1"。
 * 作为值对象，它是不可变的，通过所有属性来判断相等性。
 *
 * @param subjectType  主体类型，例如 user
 * @param subjectId    主体标识，例如 alice，与 subjectType 组合即 subject，例如 user:alice
 * @param resourceType 资源类型，例如 folder、document
 * @param resourceId   资源标识，例如 1、99，与 resourceType 组合即 Object，例如 folder:1
 * @param relationName 关系名称，例如 viewer、owner
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record RelationTuple(String subjectType, String subjectId,
                            String resourceType, String resourceId,
                            String relationName) {
}
