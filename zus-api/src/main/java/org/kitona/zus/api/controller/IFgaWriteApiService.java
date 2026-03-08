package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaTupleRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FGA 元组写入 API
 *
 * 提供 Write API，用于批量创建和删除权限元组。
 *
 * storeId 说明：来自 URL 路径，由调用方传入。获取方式：先调用 POST /fga/stores 创建或 GET /fga/stores 列出，
 * 从响应中取得 storeId；或按业务约定使用（如租户ID）。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaWriteApiService {

    /**
     * 批量写入元组
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     * @return 写入结果
     */
    @PostMapping("/write")
    RestResult<Void> write(@PathVariable String storeId, @Valid @RequestBody List<FgaTupleRequest> writes);


    /**
     * 批量删除元组
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     * @return 写入结果
     */
    @DeleteMapping("/write")
    RestResult<Void> deleted(@PathVariable String storeId, @Valid @RequestBody List<FgaTupleRequest> deletes);
}
