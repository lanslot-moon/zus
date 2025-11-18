package org.kitona.zus.api;

import org.kitona.zus.api.entity.request.ExpandApiRequest;
import org.kitona.zus.api.entity.request.ReadApiRequest;
import org.kitona.zus.api.entity.request.WriteApiRequest;
import org.kitona.zus.business.domain.request.ExpandRequest;
import org.kitona.zus.business.domain.request.ReadRequest;
import org.kitona.zus.business.domain.request.WriteRequest;
import org.kitona.zus.business.domain.response.*;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/*
 * Title: IAclApiService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 11:43
 * Description: xxx
 */
@RestController
public interface IFgaAclApiService {

    /**
     * Check API - 核心权限检查接口
     * GET /fga/v1/{namespace}/check
     */
    @GetMapping("/fga/v1/{namespace}/check")
    CompletableFuture<CheckResponse> check(@PathVariable String namespace, @RequestParam("object") String object,
                                           @RequestParam("relation") String relation, @RequestParam("user") String user);


    /**
     * Read API - 读取关系元组
     * POST /fga/v1/{namespace}/read
     */
    @PostMapping("/fga/v1/{namespace}/read")
    CompletableFuture<ReadResponse> read(@PathVariable String namespace, @RequestBody ReadApiRequest request);


    /**
     * Expand API - 展开权限集合
     * POST /fga/v1/{namespace}/expand
     */
    @PostMapping("/fga/v1/{namespace}/expand")
    CompletableFuture<ExpandResponse> expand(@PathVariable String namespace, @RequestBody ExpandApiRequest request);


    /**
     * Write API - 写入关系元组
     * POST /fga/v1/{namespace}/write
     */
    @PostMapping("/fga/v1/{namespace}/write")
    CompletableFuture<WriteResponse> write(@PathVariable String namespace, @RequestBody WriteApiRequest request);


    /**
     * 健康检查端点
     * GET /fga/v1/{namespace}/health
     */
    @GetMapping("/fga/v1/{namespace}/health")
    ServerHealthResponse health(@PathVariable String namespace);
}
