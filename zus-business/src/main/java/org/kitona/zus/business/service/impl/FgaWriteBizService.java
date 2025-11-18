package org.kitona.zus.business.service.impl;

import org.kitona.zus.business.domain.request.CheckRequest;
import org.kitona.zus.business.domain.request.WriteRequest;
import org.kitona.zus.business.domain.response.CheckResponse;
import org.kitona.zus.business.domain.response.WriteResponse;
import org.kitona.zus.business.service.IFgaCheckBizService;
import org.kitona.zus.business.service.IFgaWriteBizService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/*
 * Title: FgaWriteBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 18:48
 * Description: xxx
 */
@Service
public class FgaWriteBizService implements IFgaWriteBizService {

    @Override
    public CompletableFuture<WriteResponse> write(WriteRequest writeRequest) {
        return null;
    }
}
