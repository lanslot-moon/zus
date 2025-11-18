package org.kitona.zus.business.service.impl;

import org.kitona.zus.business.domain.request.ReadRequest;
import org.kitona.zus.business.domain.response.ReadResponse;
import org.kitona.zus.business.service.IFgaReadBizService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/*
 * Title: FgaReadBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 18:50
 * Description: xxx
 */
@Service
public class FgaReadBizService implements IFgaReadBizService {
    @Override
    public CompletableFuture<ReadResponse> read(ReadRequest readRequest) {
        return null;
    }
}
