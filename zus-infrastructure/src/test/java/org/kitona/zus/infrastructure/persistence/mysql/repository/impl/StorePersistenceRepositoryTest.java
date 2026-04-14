package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.IStoreMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StorePersistenceRepositoryTest {

    @Spy
    @InjectMocks
    private StorePersistenceRepository repository;

    @Mock
    private FgaCacheManager cacheManager;

    @Mock
    private IStoreMapper storeMapper;

    @Test
    void shouldInitZookieCacheWhenStoreCreated() {
        StorePO store = new StorePO();
        store.setStoreId("store-1");

        doReturn(true).when(repository).save(any(StorePO.class));
        ReflectionTestUtils.setField(repository, "cacheManager", cacheManager);

        assertTrue(repository.createStore(store));
        verify(cacheManager).initZookieIfAbsent("store-1", 0L);
    }
}
