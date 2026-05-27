package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.service.application.coordinator.PermissionCheckCoordinator;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.kitona.zus.service.port.IConsistencyTokenReader;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionCheckApplicationServiceTest {

    @Mock
    private PermissionCheckCoordinator permissionCheckCoordinator;

    @Mock
    private IConsistencyTokenReader consistencyTokenReader;

    private PermissionCheckApplicationService checkApplicationService;

    @BeforeEach
    void setUp() {
        checkApplicationService = new PermissionCheckApplicationService();
        ReflectionTestUtils.setField(Objects.requireNonNull(checkApplicationService), "permissionCheckCoordinator", permissionCheckCoordinator);
        ReflectionTestUtils.setField(Objects.requireNonNull(checkApplicationService), "consistencyTokenReader", consistencyTokenReader);
    }

    @Test
    void shouldMapDeniedResultToDeniedDto() {
        when(permissionCheckCoordinator.execute(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PermissionCheckResult.denied());
        when(consistencyTokenReader.currentMaxZookie("store-1")).thenReturn(7L);

        PermissionCheckResultDTO dto = checkApplicationService.check(buildCommand());

        assertFalse(dto.isAllowed());
        assertEquals("DENIED", dto.getDecision());
        assertEquals("7", dto.getZookieToken());
        assertNull(dto.getErrorMessage());
    }



    private CheckCommand buildCommand() {
        return CheckCommand.builder()
                .storeId("store-1")
                .objectType("document")
                .objectId("doc-1")
                .relation("viewer")
                .subjectType("user")
                .subjectId("alice")
                .build();
    }
}
