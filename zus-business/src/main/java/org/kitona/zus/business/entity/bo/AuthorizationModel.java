package org.kitona.zus.business.entity.bo;

import java.util.*;

/**
 * 授权模型（AuthorizationModel）
 * <p>
 * 代表整个系统当前使用的权限模型定义。
 * 在 OpenFGA 中，一个模型定义了一系列 type 以及这些 type 的 relation。
 */
public record AuthorizationModel(List<TypeDefinition> typeDefinitions) {
    // 1️⃣ 定义授权模型
    static TypeDefinition folderDefinition = new TypeDefinition(
            "folder",
            Map.of(
                    "viewer", new RelationDefinition("viewer", "self or editor or owner"),
                    "editor", new RelationDefinition("editor", "self or owner"),
                    "owner", new RelationDefinition("owner", "self"))
    );
    static TypeDefinition documentDefinition = new TypeDefinition(
            "document",
            Map.of(
                    "viewer", new RelationDefinition("viewer", "self or writer or viewer from parentFolder"),
                    // 🌟 修正 TTU 引用: <relation> from <tuple_key_relation>
                    "writer", new RelationDefinition("writer", "self or editor from parentFolder"),
                    // 🌟 修正 TTU 定义：parentFolder 现在仅是一个用于存元组的关系
                    "parentFolder", new RelationDefinition("parentFolder", "self")
            )
    );

}



