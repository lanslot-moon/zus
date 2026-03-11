package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IStoreApplicationService;
import org.kitona.zus.service.dto.query.ListStoresQuery;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.StoreResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA 存储空间 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaStoreApiService implements IFgaStoreApiService {

    @Resource
    private IStoreApplicationService storeApplicationService;

    @Override
    public RestResult<FgaStoreVO> createStore(FgaCreateStoreRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            return RestResult.fail("名称为空");
        }
        log.info("FgaStoreApiService createStore, name:{}", request.getName());
        StoreResultDTO dto = storeApplicationService.createStore(request.getName(), request.getDescription());
        FgaStoreVO convert = MapstructUtil.convert(dto, FgaStoreVO.class);
        return RestResult.success(convert);
    }

    @Override
    public RestResult<FgaStoreVO> getStore(String storeId) {
        log.debug("FgaStoreApiService getStore, storeId:{}", storeId);
        StoreResultDTO store = storeApplicationService.getStore(storeId);
        FgaStoreVO convert = MapstructUtil.convert(store, FgaStoreVO.class);
        return RestResult.success(convert);
    }

    @Override
    public RestResult<Void> disableStore(String storeId) {
        log.info("禁用存储空间: storeId={}", storeId);
        boolean success = storeApplicationService.disableStore(storeId);
        return success ? RestResult.success(null) : RestResult.fail("禁用失败，Store不存在或状态不允许");
    }

    @Override
    public RestResult<Void> enableStore(String storeId) {
        log.info("启用存储空间: storeId={}", storeId);
        boolean success = storeApplicationService.enableStore(storeId);
        return success ? RestResult.success(null) : RestResult.fail("启用失败，Store不存在或状态不允许");
    }

    @Override
    public RestResult<Void> deleteStore(String storeId) {
        log.info("删除存储空间: storeId={}", storeId);
        boolean success = storeApplicationService.deleteStore(storeId);
        return success ? RestResult.success(null) : RestResult.fail("删除失败，Store不存在或未禁用");
    }

    @Override
    public RestResult<PageResponseVO<FgaStoreVO>> listStores(Integer pageSize, String pageToken) {
        log.debug("FgaStoreApiService listStores, pageSize:{}, pageToken:{}", pageSize, pageToken);
        ListStoresQuery query = ListStoresQuery.builder()
                .pageSize(pageSize)
                .pageToken(pageToken)
                .build();
        PageResultDTO<StoreResultDTO> result = storeApplicationService.listStores(query);
        List<FgaStoreVO> voList = MapstructUtil.convert(result.getData(), FgaStoreVO.class);
        return RestResult.success(PageResponseVO.of(voList, result.getContinuationToken(), result.isHasMore()));
    }
}
