package org.kitona.zus.service.application.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionCheckCoordinatorTest {

    @Mock
    private PermissionEvaluationContextFactory evaluationContextFactory;

    @Mock
    private PermissionCheckEvaluator permissionCheckEvaluator;

    private PermissionCheckCoordinator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new PermissionCheckCoordinator();
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "evaluationContextFactory", evaluationContextFactory);
        ReflectionTestUtils.setField(Objects.requireNonNull(orchestrator), "permissionCheckEvaluator", permissionCheckEvaluator);
    }

    @Test
    void shouldReturnStoreNotFoundWhenStoreMissing() {
        when(evaluationContextFactory.create(any(), any(), any(), any()))
                .thenReturn(PermissionEvaluationContext.abnormal(PermissionCheckStatus.STORE_NOT_FOUND));

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.STORE_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelNotBoundWhenStoreHasNoCurrentModel() {
        when(evaluationContextFactory.create(any(), any(), any(), any()))
                .thenReturn(PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_BOUND));

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.MODEL_NOT_BOUND, result.status());
    }

    @Test
    void shouldReturnModelNotFoundWhenCurrentModelMissing() {
        when(evaluationContextFactory.create(any(), any(), any(), any()))
                .thenReturn(PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_NOT_FOUND));

        PermissionCheckResult result = execute();

        assertEquals(PermissionCheckStatus.MODEL_NOT_FOUND, result.status());
    }

    @Test
    void shouldReturnModelInvalidWhenTypeDefinitionsMissing() {
        when(evaluationContextFactory.create(any(), any(), any(), any()))
                .thenReturn(PermissionEvaluationContext.abnormal(PermissionCheckStatus.MODEL_INVALID));

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
