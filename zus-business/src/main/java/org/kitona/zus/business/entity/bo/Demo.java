package org.kitona.zus.business.entity.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        Map<String, RelationDefinition> folderRels = new HashMap<>();
        folderRels.put("viewer", new RelationDefinition("viewer", null));
        folderRels.put("editor", new RelationDefinition("editor", "viewer"));
        folderRels.put("owner", new RelationDefinition("owner", "editor or viewer"));

        AuthorizationModel model = new AuthorizationModel(List.of(
            new TypeDefinition("folder", folderRels, Map.of())
        ));

        AuthorizationModelGraph authorizationModelGraph = AuthorizationModelGraph.fromModel(model);
        System.out.println(authorizationModelGraph.graph());
    }
}
