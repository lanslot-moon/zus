package org.kitona.zus.business.entity.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckDemo {
    public static void main(String[] args) {
        // 1️⃣ 定义授权模型（与前文相同）
        Map<String, RelationDefinition> folderRels = new HashMap<>();
        folderRels.put("owner", new RelationDefinition("owner", "self"));
        folderRels.put("editor", new RelationDefinition("editor", "self or owner"));
        folderRels.put("viewer", new RelationDefinition("viewer", "self or editor or owner"));

        AuthorizationModel model = new AuthorizationModel(List.of(
                new TypeDefinition("folder", folderRels)
        ));

        AuthorizationModelGraph graph = AuthorizationModelGraph.fromModel(model);
        System.out.println("🔹 Model Graph:\n" + graph.graph());

        // 2️⃣ 定义关系数据
        List<RelationTuple> tuples = List.of(
                new RelationTuple("user:alice", "folder", "1", "viewer"),
                new RelationTuple("user:bob", "folder", "1", "editor")
        );

        AuthorizationChecker engine = new AuthorizationChecker(graph, tuples);

        // 3️⃣ 权限校验测试
        System.out.println("alice is viewer? " + engine.check("user:alice", "folder:1", "viewer")); // ✅ true
        System.out.println("bob is owner? " + engine.check("user:bob", "folder:1", "owner"));       // ❌ false
        System.out.println("alice is owner? " + engine.check("user:alice", "folder:1", "owner"));   // ❌ false
        System.out.println("bob is viewer? " + engine.check("user:bob", "folder:1", "viewer"));     // ✅ true
    }
}
