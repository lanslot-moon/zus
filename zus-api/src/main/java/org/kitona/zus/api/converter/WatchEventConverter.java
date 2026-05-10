package org.kitona.zus.api.converter;

import org.kitona.zus.api.response.WatchChangeEventVO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Watch 事件转换器。
 *
 * <p>负责 {@link TupleChangeResultDTO} 与 {@link WatchChangeEventVO} 之间的转换。
 * Watch 事件需要拼装 object/subject 引用字符串，因此该 mapper 使用 default 方法承载
 * API 层展示格式，不把字符串协议泄漏到应用层。
 *
 * @author kitona
 * @since 2025-02-26
 */
@Mapper(componentModel = "spring")
public interface WatchEventConverter {

    WatchEventConverter INSTANCE = Mappers.getMapper(WatchEventConverter.class);

    /**
     * 将 tuple 变更 DTO 转换为 Watch 事件 VO。
     *
     * @param dto 变更结果 DTO
     * @return 变更事件 VO，dto 为 null 时返回 null
     */
    @Mapping(target = "zookie", source = "zookie")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "tuple.object", source = ".", qualifiedByName = "objectRef")
    @Mapping(target = "tuple.relation", source = "relation")
    @Mapping(target = "tuple.subject", source = ".", qualifiedByName = "subjectRef")
    @Mapping(target = "operatorId", source = "operatorId")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "source", source = "source")
    WatchChangeEventVO toChangeEvent(TupleChangeResultDTO dto);

    /**
     * 批量转换 Watch 事件。
     *
     * @param dtoList DTO 列表
     * @return Watch 事件列表
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<WatchChangeEventVO> toChangeEventList(List<TupleChangeResultDTO> dtoList);

    @Named("objectRef")
    default String objectRef(TupleChangeResultDTO dto) {
        if (dto == null || dto.getObjectType() == null || dto.getObjectId() == null) {
            return "";
        }
        return dto.getObjectType() + ":" + dto.getObjectId();
    }

    @Named("subjectRef")
    default String subjectRef(TupleChangeResultDTO dto) {
        if (dto == null || dto.getSubjectType() == null || dto.getSubjectId() == null) {
            return "";
        }
        String base = dto.getSubjectType() + ":" + dto.getSubjectId();
        if (dto.getSubjectRelation() != null && !dto.getSubjectRelation().isEmpty()) {
            return base + "#" + dto.getSubjectRelation();
        }
        return base;
    }
}
