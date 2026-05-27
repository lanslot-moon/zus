package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.service.application.coordinator.PermissionExplainCoordinator;
import org.kitona.zus.service.application.coordinator.PermissionExplainOutcome;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason.RELATION_DENIED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionExplainApplicationServiceTest {

    @Mock
    private PermissionExplainCoordinator permissionExplainCoordinator;

    private PermissionCheckApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new PermissionCheckApplicationService();
        ReflectionTestUtils.setField(Objects.requireNonNull(applicationService), "permissionExplainCoordinator",
                permissionExplainCoordinator);
    }

    @Test
    void shouldMapExplainOutcomeToResultDto() {
        when(permissionExplainCoordinator.explain(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PermissionExplainOutcome(PermissionCheckStatus.DENIED, deniedTrace()));

        PermissionExplainResultDTO result = applicationService.explain(command());

        assertFalse(result.isAllowed());
        assertEquals("DENIED", result.getDecision());
        assertEquals("9", result.getZookieToken());
        assertNotNull(result.getResolution());
        assertEquals(StaleSnapshotDiagnosis.STALE_SNAPSHOT_CAUSED.name(),
                result.getResolution().getStaleSnapshotDiagnosis());
        assertEquals(RELATION_DENIED.name(), result.getResolution().getRoot().getReason());
    }

    private ExplainCommand command() {
        return ExplainCommand.builder()
                .storeId("store-1")
                .objectType("document")
                .objectId("doc-1")
                .relation("viewer")
                .subjectType("user")
                .subjectId("alice")
                .consistencyToken("1")
                .build();
    }

    private EvaluationTrace deniedTrace() {
        EvaluationExplainNode root = new EvaluationExplainNode(EvaluationNodeType.RELATION,
                "document:doc-1#viewer", "user:alice", "viewer", false,
                RELATION_DENIED, null, null, List.of());
        return new EvaluationTrace(false, "1", "9", StaleSnapshotDiagnosis.STALE_SNAPSHOT_CAUSED, root, false);
    }
}
