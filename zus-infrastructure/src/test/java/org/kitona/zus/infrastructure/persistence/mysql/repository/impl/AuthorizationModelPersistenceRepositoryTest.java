package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IAuthorizationModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthorizationModelPersistenceRepositoryTest {

    @Spy
    @InjectMocks
    private AuthorizationModelPersistenceRepository repository;

    @Mock
    private FgaCacheManager cacheManager;

    @Mock
    private IAuthorizationModelMapper authorizationModelMapper;

    @Test
    void shouldInvalidateCompiledModelCacheWhenModelUpdated() {
        AuthModelPO model = new AuthModelPO();
        model.setId(1L);
        model.setStoreId("store-1");
        model.setModelId("model-1");

        doReturn(true).when(repository).updateById(model);
        ReflectionTestUtils.setField(repository, "baseMapper", authorizationModelMapper);

        assertTrue(repository.updateModel(model));
        verify(cacheManager).invalidateModel("store-1", "model-1");
    }

}
