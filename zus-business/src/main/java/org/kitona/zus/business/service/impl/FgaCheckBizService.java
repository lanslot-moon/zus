package org.kitona.zus.business.service.impl;

import org.kitona.zus.business.domain.request.CheckRequest;
import org.kitona.zus.business.domain.response.CheckResponse;
import org.kitona.zus.business.service.IFgaCheckBizService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/*
 * Title: FgaCheckBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 18:49
 * Description: xxx
 */
@Service
public class FgaCheckBizService implements IFgaCheckBizService {
    @Override
    public CompletableFuture<CheckResponse> check(CheckRequest checkRequest) {
        return null;
    }
}
