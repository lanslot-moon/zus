package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationReadApplicationServiceTest {

    @Mock
    private ITupleQueryRepository tupleQueryRepository;

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

    @InjectMocks
    private AuthorizationReadApplicationService readApplicationService;

    @Test
    void shouldFilterListUsersBySubjectType() {
        CompiledAuthorizationModel compiledModel = new CompiledAuthorizationModel(Map.of(), Map.of());
        ListUsersQuery query = ListUsersQuery.builder()
                .storeId("store-1")
                .objectType("document")
                .objectId("doc-1")
                .relation("viewer")
                .subjectType("user")
                .build();

        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "demo", "desc", "model-1", null, null, null)));
        when(compiledModelCache.get("store-1", "model-1"))
                .thenReturn(Optional.of(compiledModel));
        when(permissionEvaluator.listUsers(eq(compiledModel), any()))
                .thenReturn(List.of(
                        Subject.user("user", "alice"),
                        Subject.userset("group", "eng", "member")
                ));

        ListUsersResultDTO result = readApplicationService.listUsers(query);

        assertEquals(1, result.getUsers().size());
        assertEquals("user", result.getUsers().get(0).getType());
        assertEquals("alice", result.getUsers().get(0).getId());
    }
}
