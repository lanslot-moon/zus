package org.kitona.zus.business.entity.bo;

/**
 * 关系元组（RelationTuple）
 * <p>
 * 表示一个具体的权限关系数据实例。
 * 例如 "user:alice is viewer of folder:1"。
 *
 * @param user         用户标识（subject），例如 "user:alice"
 * @param resourceType 对象类型，例如 "folder"、"document"
 * @param resourceId   对象标识，例如 "1"、"3", 和资源类型一起组合成为Object,例如 "folder:1" 或 "document:3"
 * @param relationName 关系名称，例如 "viewer"、"owner"
 */
public record RelationTuple(String user, String resourceType, String resourceId, String relationName) {
}
