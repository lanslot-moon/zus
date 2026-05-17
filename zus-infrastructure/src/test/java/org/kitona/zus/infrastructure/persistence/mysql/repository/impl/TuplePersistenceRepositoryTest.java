package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.ITupleMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TuplePersistenceRepositoryTest {

    @Spy
    @InjectMocks
    private TuplePersistenceRepository repository;

    @Mock
    private ITupleMapper tupleMapper;

    @Mock
    private FgaCacheManager cacheManager;

    @Test
    void shouldInvalidateTupleAndCheckCacheWhenBatchCreated() {
        RelationTuplePO tuple = new RelationTuplePO();
        tuple.setStoreId("store-1");
        tuple.setObjectType("document");
        tuple.setObjectId("doc-1");

        when(tupleMapper.batchInsert(any())).thenReturn(1);
        ReflectionTestUtils.setField(repository, "cacheManager", cacheManager);
        ReflectionTestUtils.setField(repository, "tupleMapper", tupleMapper);

        assertTrue(repository.batchCreate(List.of(tuple)));
        verify(cacheManager).invalidateTupleCache("store-1", "document", "doc-1");
        verify(cacheManager).invalidateCheckCache("store-1");
    }
}
