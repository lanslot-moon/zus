package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaCreateModelRequest;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.springframework.web.bind.annotation.*;

/**
 * FGA 授权模型 API
 *
 * 对应数据库表：
 * - fga_authorization_model: 授权模型主表
 * - fga_type_definition: 类型定义表
 * - fga_model_relation: 关系定义表
 * - fga_relation_restriction: 关系类型限制表
 *
 * storeId 说明：来自 URL 路径 /fga/stores/{storeId}，由调用方传入。获取方式：先调用 POST /fga/stores 创建或
 * GET /fga/stores 列出，从响应中取得 storeId；或按业务约定使用。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}/authorization-models")
public interface IFgaModelApiService {

    /**
     * 创建授权模型
     *
     * 创建后模型状态为草稿(status=0)，需调用发布接口后才能在 Check 中使用。
     *
     * @param storeId 存储空间ID，来自路径
     * @param request 创建请求，包含 schemaVersion、typeDefinitions/dslText、description
     * @return 创建结果
     */
    @PostMapping
    RestResult<Boolean> createModel(@PathVariable String storeId, @Valid @RequestBody FgaCreateModelRequest request);

    /**
     * 获取授权模型
     *
     * @param storeId 存储空间ID，来自路径
     * @param modelId 模型ID
     * @return 模型详情，包含类型定义
     */
    @GetMapping("/{modelId}")
    RestResult<FgaModelVO> getModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 列出授权模型
     *
     * @param storeId   存储空间ID，来自路径
     * @param pageSize  每页大小，默认20
     * @param pageToken 分页令牌
     * @param status    可选，按状态筛选: 0-草稿, 1-已发布, 2-已废弃
     * @return 模型列表
     */
    @GetMapping
    RestResult<PageResponseVO<FgaModelVO>> listModels(@PathVariable String storeId,
                                                      @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                                      @RequestParam(value = "page_token", required = false) String pageToken,
                                                      @RequestParam(value = "status", required = false) Integer status);

    /**
     * 发布授权模型
     *
     * <p>将草稿状态的模型发布为正式模型，发布后模型不可再修改。
     * <p><b>注意：发布不会自动激活模型</b>，需调用 {@code POST /{modelId}/activate} 将其设为当前生效模型。
     *
     * @param storeId 存储空间ID，来自路径
     * @param modelId 模型ID
     * @return 发布结果
     */
    @PostMapping("/{modelId}/publish")
    RestResult<Boolean> publishModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 激活授权模型
     *
     * <p>将已发布的模型设为 Store 的当前生效模型，后续 Check 请求使用此模型。
     * <p>前置条件：模型必须是已发布状态（status=1）
     * <p>支持切换到任意已发布的历史版本（回滚场景）。
     *
     * @param storeId 存储空间ID，来自路径
     * @param modelId 模型ID
     * @return 激活结果
     */
    @PostMapping("/{modelId}/activate")
    RestResult<Boolean> activateModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 废弃授权模型
     *
     * <p>将模型标记为废弃状态（已发布 -> 已废弃）。
     * <p>废弃后模型仍可用于历史查询，但不能被激活为当前生效模型。
     *
     * @param storeId 存储空间ID，来自路径
     * @param modelId 模型ID
     * @return 废弃结果
     */
    @PostMapping("/{modelId}/deprecate")
    RestResult<Boolean> deprecateModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 删除授权模型
     *
     * 只能删除草稿状态(status=0)的模型。
     * 已发布或已废弃的模型不允许删除，只能废弃。
     *
     * @param storeId 存储空间ID，来自路径
     * @param modelId 模型ID
     * @return 删除结果
     */
    @DeleteMapping("/{modelId}")
    RestResult<Void> deleteModel(@PathVariable String storeId, @PathVariable String modelId);
}
