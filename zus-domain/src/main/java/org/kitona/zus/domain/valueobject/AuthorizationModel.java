package org.kitona.zus.domain.valueobject;

import java.util.List;

/**
 * 授权模型值对象（Value Object）
 * <p>
 * 代表当前使用的权限模型定义，在 OpenFGA 中即一系列 type 及其 relation。
 * 作为值对象，它是不可变的。
 *
 * @param typeDefinitions 类型定义列表
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record AuthorizationModel(List<TypeDefinition> typeDefinitions) {
}
