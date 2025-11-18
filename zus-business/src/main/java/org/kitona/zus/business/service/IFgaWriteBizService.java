package org.kitona.zus.business.service;

import org.kitona.zus.business.domain.request.WriteRequest;
import org.kitona.zus.business.domain.response.WriteResponse;

import java.util.concurrent.CompletableFuture;

/*
 * Title: IFGAWriteService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 17:52
 * Description: xxx
 */
public interface IFgaWriteBizService {
    CompletableFuture<WriteResponse> write(WriteRequest writeRequest);
}
