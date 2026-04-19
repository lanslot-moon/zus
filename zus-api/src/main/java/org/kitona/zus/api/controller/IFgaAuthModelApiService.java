package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.response.FgaModelVO;
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
 * FGA Authorization Model API —— 授权模型生命周期
 *
 * <h3>URL 层级</h3>
 * <pre>
 *   /fga/stores/{storeId}/authorization-models
 *      ├── POST                                创建草稿
 *      ├── GET                                 分页列表
 *      ├── /current                  GET       当前激活模型（快捷访问）
 *      ├── /{modelId}                GET       模型详情（含 types / relations / conditions）
 *      ├── /{modelId}                DELETE    删除（仅限 DRAFT）
 *      └── /{modelId}/publish|activate|deprecate   POST 状态机跃迁
 * </pre>
 *
 * <h3>生命周期</h3>
 * <pre>
 *   DRAFT(0) ──publish──▶ PUBLISHED(1) ──deprecate──▶ ABANDONED(2)
 *      ▲                      │
 *      └── deleteModel        └── activate（激活为 Store 当前生效模型）
 * </pre>
 *
 * <h3>不可变性 &amp; 子资源设计</h3>
 * <ul>
 *   <li>模型一经发布不可修改；新版本只能通过 {@link #writeModel} 创建新 draft；</li>
 *   <li>type / relation / condition 属于模型的内部结构，<b>不单独暴露子资源 CRUD</b>：
 *       {@code getModel(view=FULL)} 返回的 {@code FgaModelVO} 已经携带完整的 types / relations / conditions 树，
 *       前端与调用方只需一次请求即可拿到模型全貌；</li>
 *   <li>如只关心 DSL 或结构化 Schema，可用 {@code view=DSL} / {@code view=SCHEMA} 精简返回。</li>
 * </ul>
 *
 * <p>对应数据库表：{@code fga_auth_model} / {@code fga_type_definition}
 * / {@code fga_relation_definition} / {@code fga_type_restriction}
 * / {@code fga_condition_definition}。
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores/{storeId}/authorization-models")
public interface IFgaAuthModelApiService {

    /**
     * 创建授权模型（草稿）。
     * <p>请求体可选 DSL 文本或结构化 Schema 二选一。成功返回新 {@code modelId}。
     */
    @PostMapping
    RestResult<FgaModelVO> writeModel(@PathVariable String storeId, @Valid @RequestBody FgaWriteAuthorizationModelRequest request);

    /**
     * 发布模型：{@code DRAFT → PUBLISHED}。发布后不能再修改。
     */
    @PostMapping("/{modelId}/publish")
    RestResult<Void> publishModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 激活模型：将已发布的模型设置为 Store 的 {@code current_model_id}。支持在多个已发布版本间回滚。
     */
    @PostMapping("/{modelId}/activate")
    RestResult<Void> activateModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 废弃模型：{@code PUBLISHED → ABANDONED}。废弃后模型不再可激活。
     */
    @PostMapping("/{modelId}/deprecate")
    RestResult<Void> deprecateModel(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 删除模型。仅允许删除 {@code DRAFT} 状态的模型。
     */
    @DeleteMapping("/{modelId}")
    RestResult<Void> deleteModel(@PathVariable String storeId, @PathVariable String modelId);

    // ========== 读 ==========

    /**
     * 获取当前激活模型（快捷入口，等价于 {@code getModel(storeId, store.currentModelId, view)}）。
     */
    @GetMapping("/current")
    RestResult<FgaModelVO> getCurrentModel(@PathVariable String storeId, @RequestParam(value = "view", defaultValue = "FULL") String view);

    /**
     * 获取指定模型详情（含 types / relations / conditions 子树）。
     *
     * @param view 视图模式：{@code DSL} / {@code SCHEMA} / {@code FULL}（默认）
     */
    @GetMapping("/{modelId}")
    RestResult<FgaModelVO> getModel(@PathVariable String storeId, @PathVariable String modelId,
                                    @RequestParam(value = "view", defaultValue = "FULL") String view);

    /**
     * 分页列出 Store 下的所有模型（仅返回模型元信息，不含 types / relations / conditions 子树）。
     *
     * @param status 可选状态筛选：0-草稿, 1-已发布, 2-已废弃
     */
    @GetMapping
    RestResult<PageResponseVO<FgaModelVO>> listModels(@PathVariable String storeId,
                                                      @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                                      @RequestParam(value = "continuation_token", required = false) String pageToken,
                                                      @RequestParam(value = "status", required = false) Integer status);
}
