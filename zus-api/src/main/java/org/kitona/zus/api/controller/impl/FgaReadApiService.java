package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaReadApiService;
import org.kitona.zus.api.converter.FgaRelationQueryConverter;
import org.kitona.zus.api.converter.FgaTupleConverter;
import org.kitona.zus.api.request.FgaListObjectsRequest;
import org.kitona.zus.api.request.FgaListSubjectsRequest;
import org.kitona.zus.api.request.FgaReadRequest;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListSubjectsResponseVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
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

        List<FgaTupleVO> tuples = FgaTupleConverter.toVOList(result.getData());
        return RestResult.success(PageResponseVO.of(tuples, result.getContinuationToken(), result.isHasMore()));
    }

    @Override
    public RestResult<FgaListObjectsResponseVO> listObjects(String storeId, FgaListObjectsRequest request) {
        log.debug("FgaReadApiService listObjects, storeId:{}, subject={}:{}, relation:{}, type:{}",
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
        return RestResult.success(FgaRelationQueryConverter.toListObjectsVO(result));
    }

    @Override
    public RestResult<FgaListSubjectsResponseVO> listSubjects(String storeId, FgaListSubjectsRequest request) {
        log.debug("FgaReadApiService listSubjects, storeId:{}, object={}:{}, relation:{}",
                storeId, request != null && request.getObject() != null ? request.getObject().getType() : null,
                request != null && request.getObject() != null ? request.getObject().getId() : null,
                request != null ? request.getRelation() : null);

        ListSubjectsQuery query = ListSubjectsQuery.builder()
                .storeId(storeId)
                .objectType(request.getObject().getType())
                .objectId(request.getObject().getId())
                .relation(request.getRelation())
                .subjectType(request.getSubjectFilter() == null ? null : request.getSubjectFilter().getType())
                .subjectRelation(request.getSubjectFilter() == null ? null : request.getSubjectFilter().getRelation())
                .consistencyToken(request.getConsistencyToken())
                .context(request.getContext())
                .build();

        ListSubjectsResultDTO result = readApplicationService.listSubjects(query);
        FgaListSubjectsResponseVO vo = new FgaListSubjectsResponseVO();
        vo.setSubjects(result == null || result.getSubjects() == null
                ? Collections.emptyList()
                : MapstructUtil.convert(result.getSubjects(), org.kitona.zus.api.response.FgaSubjectVO.class));
        return RestResult.success(vo);
    }
}
