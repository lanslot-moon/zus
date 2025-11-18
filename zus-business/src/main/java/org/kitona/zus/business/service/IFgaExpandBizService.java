package org.kitona.zus.business.service;

import org.kitona.zus.business.domain.request.ExpandRequest;
import org.kitona.zus.business.domain.response.ExpandResponse;

import java.util.concurrent.CompletableFuture;

/*
 * Title: IFGAExpandBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 17:52
 * Description: xxx
 */
public interface IFgaExpandBizService {

    CompletableFuture<ExpandResponse> expand(ExpandRequest expandRequest);
}
