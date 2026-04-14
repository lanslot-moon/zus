package org.kitona.zus.infrastructure.engine.compiler;

import org.junit.jupiter.api.Test;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompiledAuthorizationModelCompilerTest {

    private final CompiledAuthorizationModelCompiler compiler = new CompiledAuthorizationModelCompiler();

    @Test
    void shouldCompileSameModelConcurrentlyWithoutSharedState() throws Exception {
        AuthorizationModelAggregate model = aggregate("store-1", "model-1");
        var executor = Executors.newFixedThreadPool(4);
        try {
            List<Callable<CompiledAuthorizationModel>> tasks = List.of(
                    () -> compiler.compile(model),
                    () -> compiler.compile(model),
                    () -> compiler.compile(model),
                    () -> compiler.compile(model)
            );

            List<Future<CompiledAuthorizationModel>> futures = executor.invokeAll(tasks);
            CompiledAuthorizationModel first = futures.get(0).get();
            for (Future<CompiledAuthorizationModel> future : futures) {
                CompiledAuthorizationModel compiled = future.get();
                assertEquals(first.relations().keySet(), compiled.relations().keySet());
                assertEquals(first.conditions().keySet(), compiled.conditions().keySet());
                assertTrue(compiled.findRelation("document", "viewer").isPresent());
                assertTrue(compiled.findRelation("document", "editor").isPresent());
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldCompileDifferentModelsConcurrentlyWithoutCrossPollution() throws Exception {
        AuthorizationModelAggregate first = aggregate("store-1", "model-1");
        AuthorizationModelAggregate second = aggregate("store-2", "model-2");
        second.replaceConditionDefinitions(List.of(
                ConditionDefinition.reconstitute(1001L, "vpn_only", "context.vpn == true", "{}", "vpn")));

        var executor = Executors.newFixedThreadPool(2);
        try {
            Future<CompiledAuthorizationModel> firstFuture = executor.submit(() -> compiler.compile(first));
            Future<CompiledAuthorizationModel> secondFuture = executor.submit(() -> compiler.compile(second));

            CompiledAuthorizationModel firstCompiled = firstFuture.get();
            CompiledAuthorizationModel secondCompiled = secondFuture.get();

            assertEquals(Set.of("document#viewer", "document#editor"), firstCompiled.relations().keySet());
            assertEquals(Set.of("document#viewer", "document#editor"), secondCompiled.relations().keySet());
            assertTrue(firstCompiled.conditions().isEmpty());
            assertEquals(Set.of(1001L), secondCompiled.conditions().keySet());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldFailFastWhenRewriteExpressionIsInvalid() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-invalid", "1.1", "desc");
        TypeDefinition document = TypeDefinition.createWithRelations("document", List.of(
                new RelationDefinition("viewer", "self or", Set.of("user"))
        ));
        aggregate.addTypeDefinitions(List.of(document));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> compiler.compile(aggregate));
        assertTrue(exception.getMessage().contains("Invalid rewrite expression"));
        assertTrue(exception.getMessage().contains("self or"));
    }

    @Test
    void shouldSupportThisAliasInRewriteExpression() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-this", "1.1", "desc");
        TypeDefinition document = TypeDefinition.createWithRelations("document", List.of(
                new RelationDefinition("viewer", "this or editor", Set.of("user")),
                new RelationDefinition("editor", "self", Set.of("user"))
        ));
        aggregate.addTypeDefinitions(List.of(document));

        CompiledAuthorizationModel compiled = compiler.compile(aggregate);
        assertTrue(compiled.findRelation("document", "viewer").isPresent());
    }

    private AuthorizationModelAggregate aggregate(String storeId, String modelId) {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create(storeId, modelId, "1.1", "desc");
        TypeDefinition document = TypeDefinition.createWithRelations("document", List.of(
                new RelationDefinition("viewer", "self or editor", Set.of("user", "group#member")),
                new RelationDefinition("editor", "self", Set.of("user"))
        ));
        aggregate.addTypeDefinitions(List.of(document));
        return aggregate;
    }
}
