package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.repository.IChangelogQueryRepository;
import org.kitona.zus.domain.valueobject.AuthorizationCheckResult;
import org.kitona.zus.service.application.coordinator.AuthorizationCheckOrchestrator;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.CheckResultDTO;
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
class CheckApplicationServiceTest {

    @Mock
    private AuthorizationCheckOrchestrator authorizationCheckOrchestrator;

    @Mock
    private IChangelogQueryRepository changelogQueryRepository;

    private CheckApplicationService checkApplicationService;

    @BeforeEach
    void setUp() {
        checkApplicationService = new CheckApplicationService();
        ReflectionTestUtils.setField(Objects.requireNonNull(checkApplicationService), "authorizationCheckOrchestrator", authorizationCheckOrchestrator);
        ReflectionTestUtils.setField(Objects.requireNonNull(checkApplicationService), "changelogQueryRepository", changelogQueryRepository);
    }

    @Test
    void shouldMapDeniedResultToDeniedDto() {
        when(authorizationCheckOrchestrator.execute(any(), any(), any(), any(), any()))
                .thenReturn(AuthorizationCheckResult.denied());
        when(changelogQueryRepository.getMaxZookie("store-1")).thenReturn(7L);

        CheckResultDTO dto = checkApplicationService.check(buildCommand());

        assertFalse(dto.isAllowed());
        assertEquals("DENIED", dto.getDecision());
        assertEquals("7", dto.getZookieToken());
        assertNull(dto.getErrorMessage());
    }

    @Test
    void shouldExposeAbnormalAuthorizationState() {
        when(authorizationCheckOrchestrator.execute(any(), any(), any(), any(), any()))
                .thenReturn(AuthorizationCheckResult.modelNotFound());
        when(changelogQueryRepository.getMaxZookie("store-1")).thenReturn(9L);

        CheckResultDTO dto = checkApplicationService.check(buildCommand());

        assertFalse(dto.isAllowed());
        assertEquals("MODEL_NOT_FOUND", dto.getDecision());
        assertEquals("当前授权模型不存在", dto.getErrorMessage());
        assertEquals("9", dto.getZookieToken());
        assertTrue(dto.hasError());
    }

    @Test
    void shouldNotQueryZookieWhenStoreMissing() {
        when(authorizationCheckOrchestrator.execute(any(), any(), any(), any(), any()))
                .thenReturn(AuthorizationCheckResult.storeNotFound());

        CheckResultDTO dto = checkApplicationService.check(buildCommand());

        assertEquals("STORE_NOT_FOUND", dto.getDecision());
        assertEquals("", dto.getZookieToken());
        verify(changelogQueryRepository, never()).getMaxZookie(any());
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
