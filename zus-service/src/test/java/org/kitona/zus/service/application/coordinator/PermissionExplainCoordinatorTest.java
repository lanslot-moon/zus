package org.kitona.zus.service.application.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationDecision;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionExplainCoordinatorTest {

    @Mock
    private IStoreQueryRepository storeQueryRepository;

    @Mock
    private IAuthorizationModelDomainRepository modelRepository;

    @Mock
    private ICompiledModelCompiler compiledModelCompiler;

    @Mock
    private ICompiledModelCache compiledModelCache;

    @Mock
    private PermissionEvaluator permissionEvaluator;

    @Mock
    private AuthorizationModelAggregate aggregate;

    @Mock
    private CompiledAuthorizationModel compiledModel;

    private PermissionExplainCoordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new PermissionExplainCoordinator();
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "storeQueryRepository", storeQueryRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "modelRepository", modelRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "compiledModelCompiler", compiledModelCompiler);
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "compiledModelCache", compiledModelCache);
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "permissionEvaluator", permissionEvaluator);
    }

    @Test
    void shouldDiagnoseStaleSnapshotWhenLatestSnapshotAllows() {
        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "name", "desc", "model-1", 9L, 1, 1L)));
        when(modelRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(aggregate));
        when(aggregate.getTypeDefinitions()).thenReturn(List.of(TypeDefinition.create("document")));
        when(compiledModelCache.get("store-1", "model-1")).thenReturn(Optional.of(compiledModel));
        when(permissionEvaluator.checkWithExplain(any(), any())).thenReturn(deniedDecision());
        when(permissionEvaluator.check(any(), any())).thenReturn(true);

        PermissionExplainOutcome outcome = coordinator.explain(
                "store-1",
                ObjectRef.of("document", "doc-1"),
                "viewer",
                Subject.user("user", "alice"),
                Zookie.of(1L),
                Map.of()
        );

        assertEquals(PermissionCheckStatus.DENIED, outcome.status());
        assertFalse(outcome.trace().allowed());
        assertEquals("9", outcome.trace().currentZookie());
        assertEquals(StaleSnapshotDiagnosis.STALE_SNAPSHOT_CAUSED, outcome.trace().staleSnapshotDiagnosis());
    }

    private EvaluationDecision deniedDecision() {
        EvaluationExplainNode root = new EvaluationExplainNode(EvaluationNodeType.RELATION,
                "document:doc-1#viewer", "user:alice", "viewer", false,
                EvaluationExplainReason.RELATION_DENIED, null, null, List.of());
        EvaluationTrace trace = new EvaluationTrace(false, "1", "", StaleSnapshotDiagnosis.NOT_REQUESTED, root, false);
        return new EvaluationDecision(false, trace);
    }
}
