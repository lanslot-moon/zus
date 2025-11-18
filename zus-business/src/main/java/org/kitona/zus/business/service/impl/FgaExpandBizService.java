package org.kitona.zus.business.service.impl;

import org.kitona.zus.business.domain.request.ExpandRequest;
import org.kitona.zus.business.domain.response.ExpandResponse;
import org.kitona.zus.business.service.IFgaExpandBizService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/*
 * Title: FgaExpandBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 18:50
 * Description: xxx
 */
@Service
public class FgaExpandBizService implements IFgaExpandBizService {
    @Override
    public CompletableFuture<ExpandResponse> expand(ExpandRequest expandRequest) {
        return null;
    }
}
