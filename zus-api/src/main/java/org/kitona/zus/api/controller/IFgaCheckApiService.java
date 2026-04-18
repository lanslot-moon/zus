package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaCheckRequest;
import org.springframework.web.bind.annotation.*;

/**
 * FGA 权限检查 API
 *
 * 提供 Check API，用于检查用户是否对资源拥有指定权限。
 *
 * storeId 说明：来自 URL 路径，由调用方传入。获取方式：先调用 POST /fga/stores 创建或 GET /fga/stores 列出，
 * 从响应中取得 storeId；或按业务约定使用（如租户ID）。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}/authorization")
public interface IFgaCheckApiService {

    /**
     * 执行权限检查
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     * @param request 检查请求
     * @return 检查结果
     */
    @PostMapping("/check")
    RestResult<FgaCheckResultVO> check(@PathVariable String storeId, @Valid @RequestBody FgaCheckRequest request);
}
