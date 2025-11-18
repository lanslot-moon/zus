package org.kitona.zus.business.service;

import org.kitona.zus.business.domain.request.CheckRequest;
import org.kitona.zus.business.domain.response.CheckResponse;

import java.util.concurrent.CompletableFuture;

/*
 * Title: IFGACheckBizService
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/11/17 17:51
 * Description: xxx
 */
public interface IFgaCheckBizService {
    CompletableFuture<CheckResponse> check(CheckRequest checkRequest);
}
