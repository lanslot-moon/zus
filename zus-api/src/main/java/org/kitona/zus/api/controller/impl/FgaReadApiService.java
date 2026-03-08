package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaReadApiService;
import org.kitona.zus.api.request.FgaListObjectsRequest;
import org.kitona.zus.api.request.FgaListUsersRequest;
import org.kitona.zus.api.request.FgaReadRequest;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListUsersResponseVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.FgaUserVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IReadApplicationService;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.query.ReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * FGA 元组读取 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaReadApiService implements IFgaReadApiService {

    @Resource
    private IReadApplicationService readApplicationService;

    @Override
    public RestResult<PageResponseVO<FgaTupleVO>> read(String storeId, FgaReadRequest request) {
        log.debug("FgaReadApiService read, storeId:{}, objectType:{}, objectId:{}, relation:{}",
                storeId, request != null ? request.getObjectType() : null,
                request != null ? request.getObjectId() : null,
                request != null ? request.getRelation() : null);

        ReadQuery query = MapstructUtil.convert(request, ReadQuery.class);
        PageResultDTO<TupleResultDTO> result = readApplicationService.read(storeId, query);

        List<FgaTupleVO> tuples = MapstructUtil.convert(result.getData(), FgaTupleVO.class);
        return RestResult.success(PageResponseVO.of(tuples, result.getContinuationToken(), result.isHasMore()));
    }

    @Override
    public RestResult<FgaListObjectsResponseVO> listObjects(String storeId, FgaListObjectsRequest request) {
        log.debug("FgaReadApiService listObjects, storeId:{}, user={}:{}, relation:{}, type:{}",
                storeId, request != null ? request.getSubjectType() : null,
                request != null ? request.getSubjectId() : null,
                request != null ? request.getRelation() : null,
                request != null ? request.getObjectType() : null);

        ListObjectsQuery query = MapstructUtil.convert(request, ListObjectsQuery.class);
        if (query != null) {
            query.setStoreId(storeId);
        }

        ListObjectsResultDTO result = readApplicationService.listObjects(query);
        FgaListObjectsResponseVO vo = new FgaListObjectsResponseVO();
        vo.setObjects(result.getObjects() != null ? result.getObjects() : Collections.emptyList());
        return RestResult.success(vo);
    }

    @Override
    public RestResult<FgaListUsersResponseVO> listUsers(String storeId, FgaListUsersRequest request) {
        log.debug("FgaReadApiService listUsers, storeId:{}, object={}:{}, relation:{}",
                storeId, request != null ? request.getObjectType() : null,
                request != null ? request.getObjectId() : null,
                request != null ? request.getRelation() : null);

        ListUsersQuery query = MapstructUtil.convert(request, ListUsersQuery.class);
        if (query != null) {
            query.setStoreId(storeId);
        }

        ListUsersResultDTO result = readApplicationService.listUsers(query);
        List<FgaUserVO> users = MapstructUtil.convert(result.getUsers(), FgaUserVO.class);
        FgaListUsersResponseVO vo = new FgaListUsersResponseVO();
        vo.setUsers(users != null ? users : Collections.emptyList());
        return RestResult.success(vo);
    }
}
