package org.kitona.zus.business.entity.bo;

import java.util.Map;
/**
 * 类型定义（TypeDefinition）
 * <p>
 * 描述一种对象类型（如 folder/document）及其可用关系（relations）。
 */
public record TypeDefinition(String resourceType, Map<String, RelationDefinition> relations) {

    static TypeDefinition folderType = new TypeDefinition(
            "folder",
            Map.of(
                    "viewer", new RelationDefinition("viewer", "self or owner"),
                    "owner", new RelationDefinition("owner", "self")
            )
    );

}