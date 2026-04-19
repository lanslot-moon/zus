package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.FgaWriteResultVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FGA Tuple API —— 关系元组的读写与变更日志
 *
 * <h3>URL 层级</h3>
 * <pre>
 *   POST /fga/stores/{storeId}/write         事务性 writes + deletes
 *   POST /fga/stores/{storeId}/read          按任意字段过滤读取（游标分页）
 *   GET  /fga/stores/{storeId}/changes       按 zookie 拉取变更日志
 * </pre>
 *
 * <p>对应数据库表：{@code fga_relation_tuple} + {@code fga_tuple_changelog}
 *
 * <h3>审计元数据</h3>
 * 写操作要求调用方在 HTTP Header 中带上：
 * <ul>
 *   <li>{@code X-Fga-Operator-Id}：操作人标识</li>
 *   <li>{@code X-Fga-Request-Id}：请求追踪 ID（缺失时自动退化为 TraceId）</li>
 *   <li>{@code X-Fga-Source}：{@code API} / {@code SYNC} / {@code CLEANUP} / {@code MIGRATION}</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaTupleApiService {

    /**
     * 事务性写入 —— 本次请求内的 writes + deletes 原子提交。
     * <p>成功返回新 {@code zookie}，供调用方串行后续 Check 使用。
     */
    @PostMapping("/write")
    RestResult<FgaWriteResultVO> write(@PathVariable String storeId, @Valid @RequestBody FgaWriteRequest request);

    /**
     * 按 TupleKey 任意字段过滤读元组（游标分页），不经模型解析，返回直接存储的元组。
     */
    @PostMapping("/read")
    RestResult<PageResponseVO<FgaTupleVO>> read(@PathVariable String storeId, @Valid @RequestBody FgaReadRequest request);

    /**
     * 拉式变更日志 —— 按 {@code zookie} 游标分页返回 {@code fga_tuple_changelog} 中的变更事件。
     * <p>与 {@code /watch}（SSE 推模式）互补；适合批处理、审计补拉、跨网络受限环境。
     *
     * @param type      可选：按 object.type 过滤变更
     * @param startToken 起始 zookie（不含），首次可传 null
     */
    @GetMapping("/changes")
    RestResult<PageResponseVO<FgaTupleChangeVO>> listChanges(@PathVariable String storeId,
                                                             @RequestParam(value = "type", required = false) String type,
                                                             @RequestParam(value = "start_token", required = false) String startToken,
                                                             @RequestParam(value = "page_size", defaultValue = "100") Integer pageSize);
}
