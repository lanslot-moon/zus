package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.valueobject.CursorPageResult;
import org.kitona.zus.service.application.IStoreApplicationService;
import org.kitona.zus.service.assembler.StoreAssembler;
import org.kitona.zus.service.dto.query.ListStoresQuery;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.StoreResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Store 应用服务
 *
 * <p>
 * 提供存储空间的完整生命周期管理：创建、查询、禁用、启用、删除。
 *
 * <p>
 * 状态流转：
 * 
 * <pre>
 *     NORMAL ←──enable()/disable()──→ DISABLE ──delete()──→ (删除)
 * </pre>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class StoreApplicationService implements IStoreApplicationService {

    @Resource
    private IStoreDomainRepository storeRepository;

    @Override
    public StoreResultDTO createStore(String name, String description) {
        StoreAggregate store = StoreAggregate.createWithGeneratedId(name, description);
        storeRepository.saveOrUpdateStore(store);
        log.info("创建存储空间: storeId={}, name={}", store.getStoreId(), name);
        return StoreAssembler.toDTO(store);
    }

    @Override
    public StoreResultDTO getStore(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return null;
        }
        return storeRepository.findByStoreId(storeId)
                .map(StoreAssembler::toDTO)
                .orElse(null);
    }

    @Override
    public boolean disableStore(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return false;
        }
        Optional<StoreAggregate> optional = storeRepository.findByStoreId(storeId);
        if (optional.isEmpty()) {
            return false;
        }
        StoreAggregate store = optional.get();
        store.disable();
        boolean saved = storeRepository.saveOrUpdateStore(store);
        log.info("禁用存储空间: storeId={}, saved:{}", storeId, saved);
        return saved;
    }

    @Override
    public boolean enableStore(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return false;
        }
        Optional<StoreAggregate> optional = storeRepository.findByStoreId(storeId);
        if (optional.isEmpty()) {
            return false;
        }
        StoreAggregate store = optional.get();
        store.enable();
        boolean saved = storeRepository.saveOrUpdateStore(store);
        log.info("启用存储空间: storeId={}, saved:{}", storeId, saved);
        return saved;
    }

    @Override
    public boolean deleteStore(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return false;
        }
        Optional<StoreAggregate> optional = storeRepository.findByStoreId(storeId);
        if (optional.isEmpty()) {
            return false;
        }
        StoreAggregate store = optional.get();
        store.checkDeletable();
        boolean deleted = storeRepository.deleteByStoreId(storeId);
        if (deleted) {
            log.info("删除存储空间: storeId={}", storeId);
        }
        return deleted;
    }

    @Override
    public PageResultDTO<StoreResultDTO> listStores(ListStoresQuery query) {
        if (query == null) {
            query = ListStoresQuery.builder().build();
        }
        ValidationUtil.validate(query);
        CursorPageResult<StoreAggregate> pageResult = storeRepository.findPageByCursor(
                query.getPageToken(),
                query.getEffectivePageSize());
        if (pageResult.isEmpty()) {
            log.info("StoreApplicationService.listStores 查询存储空间列表为空");
            return PageResultDTO.empty();
        }
        List<StoreResultDTO> list = StoreAssembler.toDTOList(pageResult.data());
        return PageResultDTO.of(list, pageResult.nextPageToken());
    }
}
