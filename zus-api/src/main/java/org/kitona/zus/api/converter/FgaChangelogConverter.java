package org.kitona.zus.api.converter;

import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 变更日志 API 与 Service 转换器。
 *
 * <p>{@link TupleChangeResultDTO} 里的元组键位字段是扁平结构，需要在 API 层重新封装到
 * {@link FgaTupleVO} 中，使 Watch / Changes 接口的结构与 Read 接口保持一致。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Mapper(componentModel = "spring", uses = FgaTupleConverter.class)
public interface FgaChangelogConverter {

    FgaChangelogConverter INSTANCE = Mappers.getMapper(FgaChangelogConverter.class);

    /**
     * 将 tuple changelog DTO 转换为 API 响应。
     *
     * @param dto changelog DTO
     * @return API 响应
     */
    @Mapping(target = "tuple.objectType", source = "objectType")
    @Mapping(target = "tuple.objectId", source = "objectId")
    @Mapping(target = "tuple.relation", source = "relation")
    @Mapping(target = "tuple.subjectType", source = "subjectType")
    @Mapping(target = "tuple.subjectId", source = "subjectId")
    @Mapping(target = "tuple.subjectRelation", source = "subjectRelation")
    @Mapping(target = "tuple.zookie", source = "zookie")
    FgaTupleChangeVO toVO(TupleChangeResultDTO dto);

    /**
     * 批量转换 tuple changelog DTO。
     *
     * @param dtoList changelog DTO 列表
     * @return API 响应列表
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaTupleChangeVO> toVOList(List<TupleChangeResultDTO> dtoList);
}
