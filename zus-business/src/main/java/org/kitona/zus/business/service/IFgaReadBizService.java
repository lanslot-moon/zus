package org.kitona.zus.business.service;

import org.kitona.zus.business.domain.request.ReadRequest;
import org.kitona.zus.business.domain.response.ReadResponse;

import java.util.concurrent.CompletableFuture;

/*
 * Title: IFGAReadService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 17:52
 * Description: xxx
 */
public interface IFgaReadBizService {
    CompletableFuture<ReadResponse> read(ReadRequest readRequest);
}
