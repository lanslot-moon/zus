package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.valueobject.CursorPageResult;
import org.kitona.zus.service.application.IStoreApplicationService;
import org.kitona.zus.service.assembler.StoreAssembler;
import org.kitona.zus.service.dto.query.ListStoresQuery;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.StoreResultDTO;
import org.kitona.zus.service.exception.ApplicationException;
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

    @Resource
    private IStoreQueryRepository storeQueryRepository;

    @Override
    public StoreResultDTO createStore(String name, String description) {
        StoreAggregate store = StoreAggregate.createWithGeneratedId(name, description);
        boolean saved = storeRepository.saveOrUpdateStore(store);
        if (!saved) {
            log.warn("创建存储空间失败: storeId={}, name={}", store.getStoreId(), name);
            throw new ApplicationException(IError.SYSTEM_ERROR, "create store failed");
        }
        log.info("创建存储空间: storeId={}, name={}", store.getStoreId(), name);
        return storeQueryRepository.findViewByStoreId(store.getStoreId())
                .map(StoreAssembler::toDTO)
                .orElseThrow(() -> new ApplicationException(IError.DATA_NOT_EXIST,
                        "store view not found after create: " + store.getStoreId()));
    }

    @Override
    public StoreResultDTO getStore(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return null;
        }
        return storeQueryRepository.findViewByStoreId(storeId)
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
        log.info("deleteStore 删除存储空间: deleted:{}, storeId={}", deleted, storeId);
        return deleted;
    }

    @Override
    public PageResultDTO<StoreResultDTO> listStores(ListStoresQuery query) {
        query = Optional.ofNullable(query).orElse(ListStoresQuery.builder().build());

        ValidationUtil.validate(query);
        CursorPageResult<StoreView> pageResult = storeQueryRepository.findPageViewByCursor(query.getPageToken(),
                query.getEffectivePageSize());

        if (pageResult.isEmpty()) {
            log.info("StoreApplicationService.listStores 查询存储空间列表为空");
            return PageResultDTO.empty();
        }
        List<StoreResultDTO> list = StoreAssembler.toDTOViewList(pageResult.data());
        return PageResultDTO.of(list, pageResult.nextPageToken());
    }
}
