package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.request.FgaTupleMutationRequest;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.*;

/**
 * FGA 元组变更 API。
 *
 * <p>与 {@code fga_relation_tuple} 和 {@code fga_tuple_changelog} 对齐，
 * API 显式区分 tuple、condition 绑定和审计元数据。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}/tuples")
public interface IFgaWriteApiService {

    /**
     * 批量写入元组
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     * @return 写入结果
     */
    @PostMapping
    RestResult<Void> write(@PathVariable String storeId, @Valid @RequestBody FgaTupleMutationRequest request);


    /**
     * 批量删除元组
     *
     * @param storeId 存储空间ID，来自路径 /fga/stores/{storeId}，由调用方传入
     * @return 写入结果
     */
    @DeleteMapping
    RestResult<Void> delete(@PathVariable String storeId, @Valid @RequestBody FgaTupleMutationRequest request);
}
