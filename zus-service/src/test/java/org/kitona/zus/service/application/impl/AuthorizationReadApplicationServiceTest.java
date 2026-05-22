package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListObjectsEvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListSubjectsEvaluationRequest;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.service.PermissionSearchEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationReadApplicationServiceTest {

    @Mock
    private ITupleQueryPort tupleQueryRepository;

    @Mock
    private IStoreQueryPort storeQueryRepository;

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
    void shouldKeepListObjectsOptionalFiltersInEvaluationRequest() {
        CompiledAuthorizationModel compiledModel = new CompiledAuthorizationModel(Map.of(), Map.of());
        ListObjectsQuery query = ListObjectsQuery.builder()
                .storeId("store-1")
                .subjectType("group")
                .subjectId("eng")
                .subjectRelation("member")
                .relation("viewer")
                .objectType("document")
                .authorizationModelId("model-explicit")
                .build();

        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "demo", "desc", "model-current", null, null, null)));
        when(compiledModelCache.get("store-1", "model-explicit"))
                .thenReturn(Optional.of(compiledModel));
        when(permissionSearchEvaluator.listObjects(eq(compiledModel), any(ListObjectsEvaluationRequest.class)))
                .thenReturn(List.of(ObjectRef.of("document", "doc-1")));

        ListObjectsResultDTO result = readApplicationService.listObjects(query);

        ArgumentCaptor<ListObjectsEvaluationRequest> requestCaptor =
                ArgumentCaptor.forClass(ListObjectsEvaluationRequest.class);
        verify(permissionSearchEvaluator).listObjects(eq(compiledModel), requestCaptor.capture());
        assertEquals("group", requestCaptor.getValue().subjectType());
        assertEquals("eng", requestCaptor.getValue().subjectId());
        assertEquals("member", requestCaptor.getValue().subjectRelation());
        assertEquals("document", requestCaptor.getValue().objectType());
        assertEquals(List.of("document:doc-1"), result.getObjects());
        verify(compiledModelCache).get("store-1", "model-explicit");
    }

    @Test
    void shouldFilterListSubjectsBySubjectType() {
        CompiledAuthorizationModel compiledModel = new CompiledAuthorizationModel(Map.of(), Map.of());
        ListSubjectsQuery query = ListSubjectsQuery.builder()
                .storeId("store-1")
                .objectType("document")
                .objectId("doc-1")
                .relation("viewer")
                .subjectType("group")
                .subjectRelation("member")
                .authorizationModelId("model-explicit")
                .context(Map.of("tenant", "zus"))
                .build();

        when(storeQueryRepository.findViewByStoreId("store-1"))
                .thenReturn(Optional.of(new StoreView("store-1", "demo", "desc", "model-current", null, null, null)));
        when(compiledModelCache.get("store-1", "model-explicit"))
                .thenReturn(Optional.of(compiledModel));
        when(permissionSearchEvaluator.listSubjects(eq(compiledModel), any(ListSubjectsEvaluationRequest.class)))
                .thenReturn(List.of(
                        Subject.user("user", "alice")
                ));

        ListSubjectsResultDTO result = readApplicationService.listSubjects(query);

        ArgumentCaptor<ListSubjectsEvaluationRequest> requestCaptor =
                ArgumentCaptor.forClass(ListSubjectsEvaluationRequest.class);
        verify(permissionSearchEvaluator).listSubjects(eq(compiledModel), requestCaptor.capture());
        assertEquals("document", requestCaptor.getValue().object().getType());
        assertEquals("doc-1", requestCaptor.getValue().object().getId());
        assertEquals("group", requestCaptor.getValue().subjectType());
        assertEquals("member", requestCaptor.getValue().subjectRelation());
        assertEquals("zus", requestCaptor.getValue().context().get("tenant"));
        assertEquals(1, result.getSubjects().size());
        assertEquals("user", result.getSubjects().get(0).getType());
        assertEquals("alice", result.getSubjects().get(0).getId());
        verify(compiledModelCache).get("store-1", "model-explicit");
    }
}
