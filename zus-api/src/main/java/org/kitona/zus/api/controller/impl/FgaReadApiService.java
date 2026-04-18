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
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.query.TupleReadQuery;
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
    private IAuthorizationReadApplicationService readApplicationService;

    @Override
    public RestResult<PageResponseVO<FgaTupleVO>> read(String storeId, FgaReadRequest request) {
        log.debug("FgaReadApiService read, storeId:{}, objectType:{}, objectId:{}, relation:{}",
                storeId, request != null && request.getObject() != null ? request.getObject().getType() : null,
                request != null && request.getObject() != null ? request.getObject().getId() : null,
                request != null ? request.getRelation() : null);

        TupleReadQuery query = TupleReadQuery.builder()
                .objectType(request != null && request.getObject() != null ? request.getObject().getType() : null)
                .objectId(request != null && request.getObject() != null ? request.getObject().getId() : null)
                .relation(request != null ? request.getRelation() : null)
                .subjectType(request != null && request.getSubject() != null ? request.getSubject().getType() : null)
                .subjectId(request != null && request.getSubject() != null ? request.getSubject().getId() : null)
                .pageSize(request != null ? request.getPageSize() : null)
                .pageToken(request != null ? request.getPageToken() : null)
                .build();
        PageResultDTO<TupleResultDTO> result = readApplicationService.read(storeId, query);

        List<FgaTupleVO> tuples = result.getData().stream()
                .map(this::toTupleVO)
                .toList();
        return RestResult.success(PageResponseVO.of(tuples, result.getContinuationToken(), result.isHasMore()));
    }

    @Override
    public RestResult<FgaListObjectsResponseVO> listObjects(String storeId, FgaListObjectsRequest request) {
        log.debug("FgaReadApiService listObjects, storeId:{}, user={}:{}, relation:{}, type:{}",
                storeId, request != null && request.getSubject() != null ? request.getSubject().getType() : null,
                request != null && request.getSubject() != null ? request.getSubject().getId() : null,
                request != null ? request.getRelation() : null,
                request != null ? request.getObjectType() : null);

        ListObjectsQuery query = ListObjectsQuery.builder()
                .storeId(storeId)
                .subjectType(request.getSubject().getType())
                .subjectId(request.getSubject().getId())
                .subjectRelation(request.getSubject().getRelation())
                .relation(request.getRelation())
                .objectType(request.getObjectType())
                .consistencyToken(request.getConsistencyToken())
                .context(request.getContext())
                .build();

        ListObjectsResultDTO result = readApplicationService.listObjects(query);
        FgaListObjectsResponseVO vo = new FgaListObjectsResponseVO();
        vo.setObjects(result.getObjects() != null ? result.getObjects() : Collections.emptyList());
        return RestResult.success(vo);
    }

    @Override
    public RestResult<FgaListUsersResponseVO> listUsers(String storeId, FgaListUsersRequest request) {
        log.debug("FgaReadApiService listUsers, storeId:{}, object={}:{}, relation:{}",
                storeId, request != null && request.getObject() != null ? request.getObject().getType() : null,
                request != null && request.getObject() != null ? request.getObject().getId() : null,
                request != null ? request.getRelation() : null);

        ListUsersQuery query = ListUsersQuery.builder()
                .storeId(storeId)
                .objectType(request.getObject().getType())
                .objectId(request.getObject().getId())
                .relation(request.getRelation())
                .subjectType(request.getSubjectFilter() == null ? null : request.getSubjectFilter().getType())
                .subjectRelation(request.getSubjectFilter() == null ? null : request.getSubjectFilter().getRelation())
                .consistencyToken(request.getConsistencyToken())
                .context(request.getContext())
                .build();

        ListUsersResultDTO result = readApplicationService.listUsers(query);
        List<FgaUserVO> users = result.getUsers() == null ? Collections.emptyList() : result.getUsers().stream()
                .map(this::toUserVO)
                .toList();
        FgaListUsersResponseVO vo = new FgaListUsersResponseVO();
        vo.setUsers(users != null ? users : Collections.emptyList());
        return RestResult.success(vo);
    }

    private FgaTupleVO toTupleVO(TupleResultDTO dto) {
        FgaTupleVO tuple = new FgaTupleVO();
        tuple.setObjectType(dto.getObjectType());
        tuple.setObjectId(dto.getObjectId());
        tuple.setRelation(dto.getRelation());
        tuple.setSubjectType(dto.getSubjectType());
        tuple.setSubjectId(dto.getSubjectId());
        tuple.setSubjectRelation(dto.getSubjectRelation());
        return tuple;
    }

    private FgaUserVO toUserVO(ListUsersResultDTO.UserDTO dto) {
        FgaUserVO user = new FgaUserVO();
        user.setType(dto.getType());
        user.setId(dto.getId());
        user.setRelation(dto.getRelation());
        return user;
    }
}
