package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.authorization.FgaExpandRequest;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListSubjectsRequest;
import org.kitona.zus.api.response.FgaExpandTreeVO;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListSubjectsResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FGA Relation Query API —— 关系枚举 &amp; 展开（冷路径）
 *
 * <h3>定位</h3>
 * 本接口聚焦“反向 / 展开式”查询，用于管理台、授权页、审计与调试等低频场景：
 * <ul>
 *   <li>{@code listObjects}：给定 subject，列出其对某类 object 具备某 relation 的全部 object id；</li>
 *   <li>{@code listSubjects}：给定 object，列出对其具备某 relation 的全部 subject；</li>
 *   <li>{@code expand}：展开某 object 上某 relation 的 userset 树，便于人工排查。</li>
 * </ul>
 *
 * <h3>接口列表</h3>
 * <pre>
 *   POST /fga/stores/{storeId}/list-objects  主体维度反向查询
 *   POST /fga/stores/{storeId}/list-subjects    对象维度反向查询
 *   POST /fga/stores/{storeId}/expand        展开 userset 树（调试 / 审计）
 * </pre>
 *
 * <h3>特征</h3>
 * <ul>
 *   <li><b>SLA</b>：P99 百毫秒级，结果集可能较大，需分页 / 截断；</li>
 *   <li><b>缓存</b>：数据变更敏感，缓存策略与 Check 不同（通常短 TTL 或不缓存）；</li>
 *   <li><b>权限</b>：需要枚举权限，建议后台接口独立鉴权 {@code fga:enumerate}；</li>
 *   <li><b>一致性</b>：同样支持 {@code consistency} + {@code zookie}。</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaAuthViewApiService {

    /**
     * 列出主体对某类对象具备指定关系的全部 object id。
     */
    @PostMapping("/list-objects")
    RestResult<FgaListObjectsResponseVO> listObjects(@PathVariable String storeId, @Valid @RequestBody FgaListObjectsRequest request);

    /**
     * 列出对指定对象具备某关系的全部 subject。
     */
    @PostMapping("/list-subjects")
    RestResult<FgaListSubjectsResponseVO> listSubjects(@PathVariable String storeId, @Valid @RequestBody FgaListSubjectsRequest request);

    /**
     * 展开对象关系的 userset 树 —— 用于人工排查权限来源。
     */
    @PostMapping("/expand")
    RestResult<FgaExpandTreeVO> expand(@PathVariable String storeId, @Valid @RequestBody FgaExpandRequest request);
}
