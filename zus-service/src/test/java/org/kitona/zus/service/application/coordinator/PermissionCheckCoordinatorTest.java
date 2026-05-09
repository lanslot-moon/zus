package org.kitona.zus.service.application.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionCheckCoordinatorTest {

    @Mock
    private IStoreQueryRepository storeQueryRepository;

    @Mock
    private IAuthorizationModelDomainRepository modelRepository;

    @Mock
    private ICompiledModelCompiler compiledModelCompiler;

    @Mock
    private ICompiledModelCache compiledModelCache;

    @Mock
    private PermissionCheckEvaluator permissionCheckEvaluator;

    private PermissionCheckCoordinator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new PermissionCheckCoordinator();
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "storeQueryRepository", storeQueryRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "modelRepository", modelRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "compiledModelCompiler", compiledModelCompiler);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "compiledModelCache", compiledModelCache);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "permissionCheckEvaluator", permissionCheckEvaluator);
    }

    @Test
    void shouldReturnStoreNotFoundWhenStoreMissing() {
        when(storeQueryRepository.findViewByStoreId("store-1")).thenReturn(Optional.empty());

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.STORE_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelNotBoundWhenStoreHasNoCurrentModel() {
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", null, 0L, 1, 1L)));

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.MODEL_NOT_BOUND, result.status());
    }

    @Test
    void shouldReturnModelNotFoundWhenCurrentModelMissing() {
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", "model-1", 0L, 1, 1L)));
        when(modelRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.empty());

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.MODEL_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelInvalidWhenTypeDefinitionsMissing() {
        AuthorizationModelAggregate aggregate = AuthorizationModelAggregate.create("store-1", "model-1", "1.1", "desc");
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", "model-1", 0L, 1, 1L)));
        when(modelRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(aggregate));

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.MODEL_INVALID, result.status());
    }

    private PermissionCheckResult execute() {
        return orchestrator.execute(
                "store-1",
                ObjectRef.of("document", "doc-1"),
                "viewer",
                Subject.user("user", "alice"),
                Zookie.EMPTY,
                null
        );
    }
}
