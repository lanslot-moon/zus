package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaAuthViewApiService;
import org.kitona.zus.api.converter.FgaRelationQueryConverter;
import org.kitona.zus.api.request.authorization.FgaExpandRequest;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListUsersRequest;
import org.kitona.zus.api.response.FgaExpandTreeVO;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListUsersResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.springframework.stereotype.Service;

/**
 * FGA Relation Query API 实现。
 *
 * <p>{@code expand} 依赖领域层暴露 userset 树的能力，当前 {@code zus-domain} 尚未实现
 * 对应能力，先返回 501 错误，待领域 {@code PermissionEvaluator.expand} 落地后接入。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Slf4j
@Service
public class FgaRelationQueryApiService implements IFgaAuthViewApiService {

    @Resource
    private IAuthorizationReadApplicationService authorizationReadApplicationService;

    @Override
    public RestResult<FgaListObjectsResponseVO> listObjects(String storeId, FgaListObjectsRequest request) {
        log.debug("FgaRelationQueryApiService.listObjects storeId={}, relation={}, objectType={}",
                storeId, request != null ? request.getRelation() : null,
                request != null ? request.getObjectType() : null);
        ListObjectsQuery query = FgaRelationQueryConverter.toListObjectsQuery(storeId, request);
        ListObjectsResultDTO result = authorizationReadApplicationService.listObjects(query);
        return RestResult.success(FgaRelationQueryConverter.toListObjectsVO(result));
    }

    @Override
    public RestResult<FgaListUsersResponseVO> listUsers(String storeId, FgaListUsersRequest request) {
        log.debug("FgaRelationQueryApiService.listUsers storeId={}, objectType={}, relation={}",
                storeId,
                request != null && request.getObject() != null ? request.getObject().getType() : null,
                request != null ? request.getRelation() : null);
        ListUsersQuery query = FgaRelationQueryConverter.toListUsersQuery(storeId, request);
        ListUsersResultDTO result = authorizationReadApplicationService.listUsers(query);
        return RestResult.success(FgaRelationQueryConverter.toListUsersVO(result));
    }

    @Override
    public RestResult<FgaExpandTreeVO> expand(String storeId, FgaExpandRequest request) {
        log.warn("FgaRelationQueryApiService.expand 暂未实现，storeId={}", storeId);
        return RestResult.error(501, "expand 接口待领域层 PermissionEvaluator 暴露 userset 树后启用");
    }
}
