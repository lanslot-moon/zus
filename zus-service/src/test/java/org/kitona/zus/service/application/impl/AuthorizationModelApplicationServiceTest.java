package org.kitona.zus.service.application.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.RelationDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.port.IModelSnapshotRenderer;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.service.exception.ApplicationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationModelApplicationServiceTest {

    @Mock
    private IAuthorizationModelDomainRepository modelDomainRepository;

    @Mock
    private IStoreDomainRepository storeDomainRepository;

    @Mock
    private IModelSnapshotRenderer modelSnapshotRenderer;

    private AuthorizationModelApplicationService modelApplicationService;

    @BeforeEach
    void setUp() {
        modelApplicationService = new AuthorizationModelApplicationService();
        ReflectionTestUtils.setField(Objects.requireNonNull(modelApplicationService), "modelDomainRepository", modelDomainRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(modelApplicationService), "storeDomainRepository", storeDomainRepository);
        ReflectionTestUtils.setField(Objects.requireNonNull(modelApplicationService), "modelSnapshotRenderer", modelSnapshotRenderer);
    }

    @Test
    void publishModelShouldPersistPublishedAggregate() {
        StoreAggregate store = StoreAggregate.create("store-1", "store", "desc");
        AuthorizationModelAggregate model = createDraftModel("store-1", "model-1");
        when(storeDomainRepository.findByStoreId("store-1")).thenReturn(Optional.of(store));
        when(modelDomainRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(model));
        when(modelDomainRepository.saveOrUpdateModel(any(AuthorizationModelAggregate.class))).thenReturn(true);
        when(modelSnapshotRenderer.render(any(AuthorizationModelAggregate.class))).thenReturn("model model-1");

        boolean result = modelApplicationService.publishModel("store-1", "model-1");

        assertTrue(result);
        ArgumentCaptor<AuthorizationModelAggregate> captor = ArgumentCaptor.forClass(AuthorizationModelAggregate.class);
        verify(modelDomainRepository).saveOrUpdateModel(captor.capture());
        assertTrue(captor.getValue().isPublished());
        assertEquals("model model-1", captor.getValue().getDslText());
    }

    @Test
    void activateModelShouldSynchronouslyUpdateStorePointer() {
        StoreAggregate store = StoreAggregate.create("store-1", "store", "desc");
        AuthorizationModelAggregate model = createDraftModel("store-1", "model-1");
        model.publish("dsl");
        when(storeDomainRepository.findByStoreId("store-1")).thenReturn(Optional.of(store));
        when(modelDomainRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(model));
        when(storeDomainRepository.saveOrUpdateStore(any(StoreAggregate.class))).thenReturn(true);

        boolean result = modelApplicationService.activateModel("store-1", "model-1");

        assertTrue(result);
        ArgumentCaptor<StoreAggregate> captor = ArgumentCaptor.forClass(StoreAggregate.class);
        verify(storeDomainRepository).saveOrUpdateStore(captor.capture());
        assertEquals("model-1", captor.getValue().getCurrentModelId());
    }

    @Test
    void deleteModelShouldDelegateToDraftDeleteRepositoryMethod() {
        StoreAggregate store = StoreAggregate.create("store-1", "store", "desc");
        AuthorizationModelAggregate model = createDraftModel("store-1", "model-1");
        when(storeDomainRepository.findByStoreId("store-1")).thenReturn(Optional.of(store));
        when(modelDomainRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(model));
        when(modelDomainRepository.deleteDraftModel("store-1", "model-1")).thenReturn(true);

        boolean result = modelApplicationService.deleteModel("store-1", "model-1");

        assertTrue(result);
        verify(modelDomainRepository).deleteDraftModel("store-1", "model-1");
    }

    @Test
    void deleteModelShouldRejectNonDraftAggregate() {
        StoreAggregate store = StoreAggregate.create("store-1", "store", "desc");
        AuthorizationModelAggregate model = createDraftModel("store-1", "model-1");
        model.publish("dsl");
        when(storeDomainRepository.findByStoreId("store-1")).thenReturn(Optional.of(store));
        when(modelDomainRepository.findByModelId("store-1", "model-1")).thenReturn(Optional.of(model));

        assertThrows(ApplicationException.class, () -> modelApplicationService.deleteModel("store-1", "model-1"));
    }

    private AuthorizationModelAggregate createDraftModel(String storeId, String modelId) {
        AuthorizationModelAggregate model = AuthorizationModelAggregate.create(storeId, modelId, "1.1", "desc");
        TypeDefinition documentType = TypeDefinition.createWithRelations(
                "document",
                List.of(new RelationDefinition("viewer", "self", Set.of("user")))
        );
        model.addTypeDefinitions(List.of(documentType));
        return model;
    }
}
