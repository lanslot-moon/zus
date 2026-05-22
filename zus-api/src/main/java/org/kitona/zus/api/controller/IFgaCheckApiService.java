package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.response.FgaBatchCheckResultVO;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FGA Check API —— 权限判定
 *
 * <h3>定位</h3>
 * 本接口只包含“给定 TupleKey，是否允许”的判定语义，对应 Zanzibar 中的 Check / BatchCheck 原语。
 * 业务侧的鉴权拦截、网关放行、UI 按钮可见性等高频调用都走这两个方法。
 *
 * <h3>接口列表</h3>
 * <pre>
 *   POST /fga/stores/{storeId}/check         单次判断
 *   POST /fga/stores/{storeId}/batch-check   批量判断（一次请求、多条 TupleKey）
 * </pre>
 *
 * <h3>特征</h3>
 * <ul>
 *   <li><b>SLA</b>：P99 &lt; 10ms（热路径，通常带缓存）；</li>
 *   <li><b>一致性</b>：支持 {@code consistency} 参数 + {@code zookie} 透传，保证读己之写；</li>
 *   <li><b>ABAC 求值</b>：请求体可附带 {@code context}，交由模型中的 condition 表达式求值；</li>
 *   <li><b>回归</b>：可选 {@code authorizationModelId} 固定到特定版本，用于灰度 / 回滚；</li>
 *   <li><b>权限</b>：通常只需要 {@code fga:check}，无需枚举权限。</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaCheckApiService {

    /**
     * 单次权限检查 —— 判定 {@code subject} 对 {@code object} 是否具备 {@code relation}。
     */
    @PostMapping("/check")
    RestResult<FgaCheckResultVO> check(@PathVariable String storeId, @Valid @RequestBody FgaCheckRequest request);

    /**
     * 解释一次权限检查的证明过程。
     */
    @PostMapping("/check/explain")
    RestResult<FgaExplainResultVO> explain(@PathVariable String storeId, @Valid @RequestBody FgaExplainRequest request);

    /**
     * 批量权限检查 —— 一次请求、多条 TupleKey；用于渲染权限列表 / 批量过滤。
     */
    @PostMapping("/batch-check")
    RestResult<FgaBatchCheckResultVO> batchCheck(@PathVariable String storeId, @Valid @RequestBody FgaBatchCheckRequest request);
}
