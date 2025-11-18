package org.kitona.zus.client.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * Title: IFgaAclFacade
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 17:28
 * Description: xxx
 */
public interface IFgaAclFacade {

    /**
     * 分布式聚合查询 - 内部RPC调用使用
     * 集群间工作分发和结果聚合
     * POST /fga/v1/{namespace}/internal/aggregate-check
     */
//    CompletableFuture<CheckResponse> aggregateCheck(String namespace,List<CheckRequest> requests);
}
