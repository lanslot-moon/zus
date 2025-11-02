package org.kitona.zus.business.entity.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexCheckDemo {
    public static void main(String[] args) {
        // 1️⃣ 定义授权模型
        Map<String, RelationDefinition> folderRels = new HashMap<>();
        folderRels.put("viewer", new RelationDefinition("viewer", "self or editor or owner"));
        folderRels.put("editor", new RelationDefinition("editor", "self or owner"));
        folderRels.put("owner", new RelationDefinition("owner", "self"));

        Map<String, RelationDefinition> documentRels = new HashMap<>();
        documentRels.put("viewer", new RelationDefinition("viewer", "self or writer or parentFolder#viewer"));
        documentRels.put("writer", new RelationDefinition("writer", "self or parentFolder#editor"));
        documentRels.put("parentFolder", new RelationDefinition("parentFolder", "tupleToUserset: from=document#parentFolder; to=folder#owner"));

        AuthorizationModel model = new AuthorizationModel(List.of(
                new TypeDefinition("folder", folderRels),
                new TypeDefinition("document", documentRels)
        ));

        AuthorizationModelGraph graph = AuthorizationModelGraph.fromModel(model);
        System.out.println("🔹 Model Graph:\n" + graph.graph());

        // 2️⃣ 定义关系数据（元组）
        List<RelationTuple> tuples = List.of(
                // folder:1 的关系
                new RelationTuple("user:alice", "folder", "1", "owner"),
                new RelationTuple("user:bob", "folder", "1", "editor"),
                new RelationTuple("user:carol", "folder", "1", "viewer"),

                // document:99 属于 folder:1
                new RelationTuple("folder:1", "document", "99", "parentFolder"),

                // document:99 的直接绑定
                new RelationTuple("user:david", "document", "99", "writer")
        );

        AuthorizationChecker engine = new AuthorizationChecker(graph, tuples);

        // 3️⃣ 权限校验测试
        System.out.println("\n🔸 权限校验结果:");
//        System.out.println("alice is owner of folder:1 ? " + engine.check("user:alice", "folder:1", "owner")); // ✅ true
//        System.out.println("bob is viewer of folder:1 ? " + engine.check("user:bob","folder:1", "viewer"));   // ✅ true (editor 继承 viewer)
//        System.out.println("carol is editor of folder:1 ? " + engine.check("user:carol","folder:1", "editor")); // ❌ false
//        System.out.println();
//        System.out.println("david is viewer of document:99 ? " + engine.check("user:david", "document:99", "viewer")); // ✅ true (writer → viewer)
        System.out.println("alice is writer of document:99 ? " + engine.check("user:alice","document:99", "writer")); // ✅ true (通过 parentFolder#editor 继承)
        System.out.println("bob is viewer of document:99 ? " + engine.check("user:bob", "document:99", "viewer"));     // ✅ true (folder editor → doc viewer)
        System.out.println("carol is writer of document:99 ? " + engine.check("user:carol","document:99", "writer")); // ❌ false
        System.out.println("alice is viewer of document:99 ? " + engine.check("user:alice", "document:99", "viewer")); // ✅ true (folder owner → doc viewer)
        System.out.println("eve is viewer of document:99 ? " + engine.check("user:eve", "document:99", "viewer"));     // ❌ false
    }
}
