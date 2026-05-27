package org.kitona.zus.service.application.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationDecision;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.RELATION_DENIED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionExplainCoordinatorTest {

    @Mock
    private PermissionEvaluationContextFactory evaluationContextFactory;

    @Mock
    private PermissionCheckEvaluator permissionCheckEvaluator;

    @Mock
    private CompiledAuthorizationModel compiledModel;

    private PermissionExplainCoordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new PermissionExplainCoordinator();
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "evaluationContextFactory", evaluationContextFactory);
        ReflectionTestUtils.setField(Objects.requireNonNull(coordinator), "permissionCheckEvaluator", permissionCheckEvaluator);
    }

    @Test
    void shouldDiagnoseStaleSnapshotWhenLatestSnapshotAllows() {
        when(evaluationContextFactory.create(any(), any(), any(), any()))
                .thenReturn(PermissionEvaluationContext.ready(
                        new StoreView("store-1", "name", "desc", "model-1", 9L, 1, 1L),
                        compiledModel,
                        null
                ));
        when(permissionCheckEvaluator.checkWithExplain(any(), any())).thenReturn(deniedDecision());
        when(permissionCheckEvaluator.check(any(), any())).thenReturn(true);

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
                RELATION_DENIED, null, null, List.of());
        EvaluationTrace trace = new EvaluationTrace(false, "1", "", StaleSnapshotDiagnosis.NOT_REQUESTED, root, false);
        return new EvaluationDecision(false, trace);
    }
}
