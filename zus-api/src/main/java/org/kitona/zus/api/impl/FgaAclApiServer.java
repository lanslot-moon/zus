package org.kitona.zus.api.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.api.IFgaAclApiService;
import org.kitona.zus.api.entity.request.ExpandApiRequest;
import org.kitona.zus.api.entity.request.ReadApiRequest;
import org.kitona.zus.api.entity.request.WriteApiRequest;
import org.kitona.zus.business.domain.request.CheckRequest;
import org.kitona.zus.business.domain.request.ExpandRequest;
import org.kitona.zus.business.domain.request.ReadRequest;
import org.kitona.zus.business.domain.request.WriteRequest;
import org.kitona.zus.business.domain.response.*;
import org.kitona.zus.business.service.IFgaCheckBizService;
import org.kitona.zus.business.service.IFgaExpandBizService;
import org.kitona.zus.business.service.IFgaReadBizService;
import org.kitona.zus.business.service.IFgaWriteBizService;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.MapstructUtil;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * ACL Server - Zanzibar核心服务器
 * 处理Check、Read、Expand、Write API请求
 */
@Slf4j
@Service
public class FgaAclApiServer implements IFgaAclApiService {

    @Resource
    private IFgaReadBizService fgaReadBizService;

    @Resource
    private IFgaWriteBizService fgaWriteBizService;

    @Resource
    private IFgaCheckBizService fgaCheckBizService;

    @Resource
    private IFgaExpandBizService fgaExpandBizService;

    @Override
    public CompletableFuture<CheckResponse> check(String namespace, String object, String relation, String user) {
        if (StringUtils.isAnyBlank(namespace, object, relation, user)) {
            throw new IllegalArgumentException("namespace, object, relation, user不能为空");
        }

        CheckRequest request = CheckRequest.builder().namespace(namespace)
                .object(object)
                .relation(relation)
                .user(user)
                .build();
        log.info("FgaAclApiServer.check [ACL-SERVER] Check请求 request:{}", JacksonUtil.toJSONString(request));
        return fgaCheckBizService.check(request);
    }

    @Override
    public CompletableFuture<ReadResponse> read(String namespace, ReadApiRequest request) {
        if (StringUtils.isAnyBlank(namespace)) {
            throw new IllegalArgumentException("namespace不能为空");
        }

        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }

        request.setNamespace(namespace);
        log.info("fgaAclApiServer.read [ACL-SERVER] Read请求: namespace={}, request:{}", namespace, JacksonUtil.toJSONString(request));
        ReadRequest convert = MapstructUtil.convert(request, ReadRequest.class);
        return fgaReadBizService.read(convert);
    }

    @Override
    public CompletableFuture<ExpandResponse> expand(String namespace, ExpandApiRequest request) {
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace不能为空");
        }

        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }

        request.setNamespace(namespace);
        log.info("fgaAclApiServer.expand [ACL-SERVER] Expand请求, namespace:{}, request:{}", namespace, JacksonUtil.toJSONString(request));
        ExpandRequest convert = MapstructUtil.convert(request, ExpandRequest.class);
        return fgaExpandBizService.expand(convert);
    }

    @Override
    public CompletableFuture<WriteResponse> write(String namespace, WriteApiRequest request) {
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace不能为空");
        }

        if (request == null) {
            throw new IllegalArgumentException("request不能为空");
        }

        request.setNamespace(namespace);
        log.info("fgaAclApiServer.write [ACL-SERVER] Write请求, namespace:{}, request:{}", namespace, JacksonUtil.toJSONString(request));
        WriteRequest convert = MapstructUtil.convert(request, WriteRequest.class);
        return fgaWriteBizService.write(convert);
    }

    @Override
    public ServerHealthResponse health(String namespace) {
        return ServerHealthResponse.builder()
                .namespace(namespace)
                .status("healthy")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}