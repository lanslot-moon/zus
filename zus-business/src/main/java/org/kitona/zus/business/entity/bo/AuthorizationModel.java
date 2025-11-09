package org.kitona.zus.business.entity.bo;

import java.util.*;

/**
 * 授权模型（AuthorizationModel）
 * <p>
 * 代表整个系统当前使用的权限模型定义。
 * 在 OpenFGA 中，一个模型定义了一系列 type 以及这些 type 的 relation。
 */
public record AuthorizationModel(List<TypeDefinition> typeDefinitions) {
}



