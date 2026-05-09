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
import org.kitona.zus.domain.service.PermissionSearchEvaluator;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
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
    private PermissionSearchEvaluator permissionSearchEvaluator;

    @InjectMocks
    private AuthorizationReadApplicationService readApplicationService;

    @Test
    void shouldFilterListSubjectsBySubjectType() {
        CompiledAuthorizationModel compiledModel = new CompiledAuthorizationModel(Map.of(), Map.of());
        ListSubjectsQuery query = ListSubjectsQuery.builder()
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
        when(permissionSearchEvaluator.listSubjects(eq(compiledModel), eq("store-1"), any(), eq("viewer"), any(), any()))
                .thenReturn(List.of(
                        Subject.user("user", "alice"),
                        Subject.userset("group", "eng", "member")
                ));

        ListSubjectsResultDTO result = readApplicationService.listSubjects(query);

        assertEquals(1, result.getSubjects().size());
        assertEquals("user", result.getSubjects().get(0).getType());
        assertEquals("alice", result.getSubjects().get(0).getId());
    }
}
