package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FGA Store API —— 存储空间生命周期
 *
 * <p>对应数据库表：{@code fga_store}
 *
 * <p>Store 是 FGA 的顶层隔离单元；所有其他 FGA API 都以 {@code /fga/stores/{storeId}/...} 为前缀。
 * 关于 {@code storeId} 的来源见 {@code zus-api/docs/FGA_STORE_ID.md}。
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores")
public interface IFgaStoreApiService {

    /**
     * 创建 Store。
     * <p>初始状态 {@code status = NORMAL(0)}，自动分配 {@code storeId}。
     */
    @PostMapping
    RestResult<FgaStoreVO> createStore(@Valid @RequestBody FgaCreateStoreRequest request);

    /**
     * 获取 Store 详情
     */
    @GetMapping("/{storeId}")
    RestResult<FgaStoreVO> getStore(@PathVariable String storeId);

    /**
     * 游标分页列出 Store
     */
    @GetMapping
    RestResult<PageResponseVO<FgaStoreVO>> listStores(@RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                                      @RequestParam(value = "continuation_token", required = false) String pageToken);

    /**
     * 启用 Store：{@code DISABLE → NORMAL}
     */
    @PostMapping("/{storeId}/enable")
    RestResult<Void> enableStore(@PathVariable String storeId);

    /**
     * 禁用 Store：{@code NORMAL → DISABLE}。禁用后不参与 Check。
     */
    @PostMapping("/{storeId}/disable")
    RestResult<Void> disableStore(@PathVariable String storeId);

    /**
     * 删除 Store。前置条件：{@code DISABLE} 状态。删除后不可恢复。
     */
    @DeleteMapping("/{storeId}")
    RestResult<Void> deleteStore(@PathVariable String storeId);
}
