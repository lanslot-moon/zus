package org.kitona.zus.api.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListSubjectsRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListSubjectsResponseVO;
import org.kitona.zus.api.response.FgaSubjectVO;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 关系查询（ListObjects / ListSubjects）的 API 与 Service 转换器。
 *
 * <p>当前 {@link FgaListSubjectsRequest} 支持多个 subjectFilters，但应用层
 * {@link ListSubjectsQuery} 只承载单一过滤条件；这里选取第一个过滤器作为主过滤项，
 * 后续若应用层支持 multi-filter，可在此处集中调整。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Mapper(componentModel = "spring")
public interface FgaRelationQueryConverter {

    FgaRelationQueryConverter INSTANCE = Mappers.getMapper(FgaRelationQueryConverter.class);

    /**
     * 将 ListObjects API 请求转换为应用层查询对象。
     *
     * @param storeId Store 标识
     * @param request API 请求
     * @return 应用层查询
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "subjectType", source = "request.subject.type")
    @Mapping(target = "subjectId", source = "request.subject.id")
    @Mapping(target = "subjectRelation", source = "request.subject.relation")
    @Mapping(target = "relation", source = "request.relation")
    @Mapping(target = "objectType", source = "request.objectType")
    @Mapping(target = "authorizationModelId", source = "request.authorizationModelId")
    @Mapping(target = "consistencyToken", source = "request.consistency", qualifiedByName = "resolveConsistencyToken")
    @Mapping(target = "context", source = "request.context")
    ListObjectsQuery toListObjectsQuery(String storeId, FgaListObjectsRequest request);

    /**
     * 将 ListSubjects API 请求转换为应用层查询对象。
     *
     * @param storeId Store 标识
     * @param request API 请求
     * @return 应用层查询
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "objectType", source = "request.object.type")
    @Mapping(target = "objectId", source = "request.object.id")
    @Mapping(target = "relation", source = "request.relation")
    @Mapping(target = "subjectType", source = "request.subjectFilters", qualifiedByName = "firstSubjectType")
    @Mapping(target = "subjectRelation", source = "request.subjectFilters", qualifiedByName = "firstSubjectRelation")
    @Mapping(target = "authorizationModelId", source = "request.authorizationModelId")
    @Mapping(target = "consistencyToken", source = "request.consistency", qualifiedByName = "resolveConsistencyToken")
    @Mapping(target = "context", source = "request.context")
    ListSubjectsQuery toListSubjectsQuery(String storeId, FgaListSubjectsRequest request);

    /**
     * 将应用层 ListObjects 结果转换为 API 响应。
     *
     * @param dto 应用层结果
     * @return API 响应
     */
    @Mapping(target = "objects", source = "objects")
    FgaListObjectsResponseVO toListObjectsVO(ListObjectsResultDTO dto);

    /**
     * 将应用层 ListSubjects 结果转换为 API 响应。
     *
     * @param dto 应用层结果
     * @return API 响应
     */
    @Mapping(target = "subjects", source = "subjects")
    FgaListSubjectsResponseVO toListSubjectsVO(ListSubjectsResultDTO dto);

    /**
     * 将主体 DTO 列表转换为主体 VO 列表。
     *
     * <p>这里显式声明元素转换，避免 MapStruct 在 Lombok 编译顺序下无法识别内部类属性。
     *
     * @param subjects 应用层主体 DTO 列表
     * @return API 主体 VO 列表
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaSubjectVO> toSubjectVOList(List<ListSubjectsResultDTO.SubjectDTO> subjects);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "relation", source = "relation")
    FgaSubjectVO toSubjectVO(ListSubjectsResultDTO.SubjectDTO subject);

    @Named("firstSubjectType")
    default String firstSubjectType(List<FgaListSubjectsRequest.SubjectFilter> filters) {
        FgaListSubjectsRequest.SubjectFilter filter = firstFilter(filters);
        return filter == null ? null : filter.getType();
    }

    @Named("firstSubjectRelation")
    default String firstSubjectRelation(List<FgaListSubjectsRequest.SubjectFilter> filters) {
        FgaListSubjectsRequest.SubjectFilter filter = firstFilter(filters);
        return filter == null ? null : filter.getRelation();
    }

    private FgaListSubjectsRequest.SubjectFilter firstFilter(List<FgaListSubjectsRequest.SubjectFilter> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return null;
        }
        return filters.get(0);
    }

    @Named("resolveConsistencyToken")
    default String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null || options.getPreference() != FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return null;
        }
        return options.getAtRevision();
    }
}
