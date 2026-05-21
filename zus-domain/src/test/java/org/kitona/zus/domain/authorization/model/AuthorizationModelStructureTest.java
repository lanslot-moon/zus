package org.kitona.zus.domain.authorization.model;

import org.junit.jupiter.api.Test;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.domain.enums.ModelPublishStatus;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationModelStructureTest {

    @Test
    void fromTypesAndConditionsShouldRejectDuplicateTypeNames() {
        TypeDefinition document = TypeDefinition.create("document");
        TypeDefinition duplicate = TypeDefinition.create("document");

        assertThrows(SystemException.class, () ->
                AuthorizationModelStructure.fromTypesAndConditions(List.of(document, duplicate), List.of()));
    }

    @Test
    void replaceStructureShouldPreserveSortOrder() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-1");
        TypeDefinition user = TypeDefinition.create("user");
        TypeDefinition document = TypeDefinition.create("document");

        aggregate.replaceStructure(AuthorizationModelStructure.fromTypesAndConditions(
                List.of(document, user), List.of()));

        List<TypeDefinition> ordered = aggregate.getTypeDefinitions();
        assertEquals(2, ordered.size());
        assertEquals("document", ordered.get(0).getSubjectType());
        assertEquals("user", ordered.get(1).getSubjectType());
        assertEquals(0, ordered.get(0).getSortOrder());
        assertEquals(1, ordered.get(1).getSortOrder());
    }

    @Test
    void publishRequiresAtLeastOneType() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-1");
        aggregate.replaceStructure(AuthorizationModelStructure.empty());

        assertThrows(SystemException.class, () -> aggregate.publish("model dsl"));
    }

    @Test
    void publishSucceedsWhenStructureHasTypes() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-1");
        TypeDefinition document = TypeDefinition.createWithRelations("document",
                List.of(new RelationDefinition("viewer", "self", Set.of("user"))));
        aggregate.replaceStructure(AuthorizationModelStructure.fromTypesAndConditions(
                List.of(document), List.of()));

        aggregate.publish("dsl snapshot");

        assertTrue(aggregate.isPublished());
        assertEquals(ModelPublishStatus.PUBLISHED, aggregate.getStatus());
    }
}
