package org.kitona.zus.business.entity.bo;

/**
 * 关系定义（RelationDefinition）
 * <p>
 * 描述对象类型中单个 relation（如 viewer）的访问逻辑。
 *
 * @param relationName 关系名，例如 "viewer"、"editor"、"owner"
 * @param rewriteExpression 关系定义表达式，描述访问规则
 *                          简化写法支持：
 *                          - "self"（直接 tuple）
 *                          - "self or X"（computed userset）
 *                          - "tupleToUserset: from=A#B; to=C#D"（跨对象映射）
 */
public record RelationDefinition(String relationName, String rewriteExpression) {

}
