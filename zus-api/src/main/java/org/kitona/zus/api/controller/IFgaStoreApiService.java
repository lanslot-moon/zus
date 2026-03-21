package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.springframework.web.bind.annotation.*;

/**
 * FGA 存储空间 API
 *
 * 管理存储空间（Store）的创建、查询、删除和列表。Store 是权限数据的顶层隔离单元；
 * 创建或列出 Store 后得到的 storeId，用于其他 FGA 接口的 URL 路径（如 /fga/stores/{storeId}/check）。
 * 调用方在其他 API 中使用的 storeId 即来源于本接口的创建或列表响应。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores")
public interface IFgaStoreApiService {

    /**
     * 创建存储空间
     */
    @PostMapping
    RestResult<FgaStoreVO> createStore(@Valid @RequestBody FgaCreateStoreRequest request);

    /**
     * 获取存储空间信息
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     */
    @GetMapping("/{storeId}")
    RestResult<FgaStoreVO> getStore(@PathVariable String storeId);

    /**
     * 禁用存储空间
     *
     * <p>
     * 状态流转：NORMAL → DISABLE
     * <p>
     * 禁用后 Store 不可用于权限检查，但数据保留，可随时恢复。
     *
     * @param storeId 存储空间ID，由调用方传入
     */
    @PostMapping("/{storeId}/disable")
    RestResult<Void> disableStore(@PathVariable String storeId);

    /**
     * 启用存储空间
     *
     * <p>
     * 状态流转：DISABLE → NORMAL
     * <p>
     * 启用后 Store 恢复正常使用。
     *
     * @param storeId 存储空间ID，由调用方传入
     */
    @PostMapping("/{storeId}/enable")
    RestResult<Void> enableStore(@PathVariable String storeId);

    /**
     * 删除存储空间
     *
     * <p>
     * 前置条件：Store 必须是 DISABLE 状态
     * <p>
     * 必须先禁用再删除，删除后不可恢复。
     *
     * @param storeId 存储空间ID，由调用方传入
     */
    @DeleteMapping("/{storeId}")
    RestResult<Void> deleteStore(@PathVariable String storeId);

    /**
     * 游标分页列出存储空间
     *
     * @param pageSize  每页大小，默认20
     * @param pageToken 分页游标（首页不传）
     * @return 分页结果
     */
    @GetMapping
    RestResult<PageResponseVO<FgaStoreVO>> listStores(
            @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "continuation_token", required = false) String pageToken);
}
