package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListObjectsEvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListSubjectsEvaluationRequest;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.service.PermissionSearchEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.service.application.coordinator.CompiledAuthorizationModelLoader;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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
    private CompiledAuthorizationModelLoader compiledAuthorizationModelLoader;

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

        when(compiledAuthorizationModelLoader.load("store-1", "model-explicit"))
                .thenReturn(compiledModel);
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
        verify(compiledAuthorizationModelLoader).load("store-1", "model-explicit");
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

        when(compiledAuthorizationModelLoader.load("store-1", "model-explicit"))
                .thenReturn(compiledModel);
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
        verify(compiledAuthorizationModelLoader).load("store-1", "model-explicit");
    }
}
