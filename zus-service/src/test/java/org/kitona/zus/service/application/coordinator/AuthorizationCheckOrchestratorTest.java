package org.kitona.zus.service.application.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.query.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.valueobject.AuthorizationCheckResult;
import org.kitona.zus.domain.valueobject.AuthorizationCheckStatus;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.port.ITupleStoreFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationCheckOrchestratorTest {

    @Mock
    private IStoreQueryRepository storeQueryRepository;

    @Mock
    private IAuthorizationModelDomainRepository modelRepository;

    @Mock
    private ITupleStoreFactory tupleStoreFactory;

    @Mock
    private IModelCompiler modelCompiler;

    private AuthorizationCheckOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new AuthorizationCheckOrchestrator();
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "storeQueryRepository", storeQueryRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "modelRepository", modelRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "tupleStoreFactory", tupleStoreFactory);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "modelCompiler", modelCompiler);
    }

    @Test
    void shouldReturnStoreNotFoundWhenStoreMissing() {
        when(storeQueryRepository.findViewByStoreId("store-1")).thenReturn(Optional.empty());

        AuthorizationCheckResult result = execute();

        assertEquals(AuthorizationCheckStatus.STORE_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelNotBoundWhenStoreHasNoCurrentModel() {
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", null, 0L, 1, 1L)));

        AuthorizationCheckResult result = execute();

        assertEquals(AuthorizationCheckStatus.MODEL_NOT_BOUND, result.status());
    }

    @Test
    void shouldReturnModelNotFoundWhenCurrentModelMissing() {
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", "model-1", 0L, 1, 1L)));
        when(modelRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.empty());

        AuthorizationCheckResult result = execute();

        assertEquals(AuthorizationCheckStatus.MODEL_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelInvalidWhenTypeDefinitionsMissing() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-1", "1.1", "dsl", "desc");
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", "model-1", 0L, 1, 1L)));
        when(modelRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(aggregate));

        AuthorizationCheckResult result = execute();

        assertEquals(AuthorizationCheckStatus.MODEL_INVALID, result.status());
    }

    private AuthorizationCheckResult execute() {
        return orchestrator.execute(
                "store-1",
                ObjectRef.of("document", "doc-1"),
                "viewer",
                Subject.user("user", "alice"),
                Zookie.EMPTY
        );
    }
}
