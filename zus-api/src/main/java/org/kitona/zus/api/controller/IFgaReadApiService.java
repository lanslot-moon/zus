package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaListObjectsRequest;
import org.kitona.zus.api.request.FgaListSubjectsRequest;
import org.kitona.zus.api.request.FgaReadRequest;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListSubjectsResponseVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.springframework.web.bind.annotation.*;

/**
 * FGA 元组读取 API（Read / ListObjects / ListSubjects）
 *
 * storeId 说明：来自 URL 路径 /fga/stores/{storeId}，由调用方传入。获取方式：先调用 POST /fga/stores 创建或
 * GET /fga/stores 列出，从响应中取得 storeId；或按业务约定使用。详见 zus-api/docs/FGA_STORE_ID.md。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}")
public interface IFgaReadApiService {

    /**
     * 读取元组
     *
     * @param storeId 存储空间ID，来自路径，由调用方传入
     */
    @PostMapping("/tuples/query")
    RestResult<PageResponseVO<FgaTupleVO>> read(@PathVariable String storeId,
                                                @Valid @RequestBody FgaReadRequest request);

    /**
     * 列出主体可访问的对象。
     *
     * @param storeId 存储空间ID，来自路径，由调用方传入
     */
    @PostMapping("/authorization/list-objects")
    RestResult<FgaListObjectsResponseVO> listObjects(@PathVariable String storeId,
                                                     @Valid @RequestBody FgaListObjectsRequest request);

    /**
     * 列出对对象关系具备权限的主体。
     *
     * @param storeId 存储空间ID，来自路径，由调用方传入
     */
    @PostMapping("/authorization/list-subjects")
    RestResult<FgaListSubjectsResponseVO> listSubjects(@PathVariable String storeId,
                                                 @Valid @RequestBody FgaListSubjectsRequest request);
}
