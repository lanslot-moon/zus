package org.kitona.zus.api.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * 变更日志 API ↔ Service 转换器。
 *
 * <p>{@link TupleChangeResultDTO} 里的元组键位字段是扁平的，需要在 API 层重新封装到
 * {@link FgaTupleVO} 中，使 Watch / Changes 接口的结构与 Read 接口保持一致。
 *
 * @author kitona
 * @since 2026-04-18
 */
public final class FgaChangelogConverter {

    private FgaChangelogConverter() {
    }

    public static FgaTupleChangeVO toVO(TupleChangeResultDTO dto) {
        if (dto == null) {
            return null;
        }
        FgaTupleVO tuple = new FgaTupleVO();
        tuple.setObjectType(dto.getObjectType());
        tuple.setObjectId(dto.getObjectId());
        tuple.setRelation(dto.getRelation());
        tuple.setSubjectType(dto.getSubjectType());
        tuple.setSubjectId(dto.getSubjectId());
        tuple.setSubjectRelation(dto.getSubjectRelation());
        tuple.setZookie(dto.getZookie());

        return FgaTupleChangeVO.builder()
                .zookie(dto.getZookie())
                .operation(dto.getOperation())
                .tuple(tuple)
                .operationTime(dto.getOperationTime())
                .operatorId(dto.getOperatorId())
                .requestId(dto.getRequestId())
                .source(dto.getSource())
                .build();
    }

    public static List<FgaTupleChangeVO> toVOList(List<TupleChangeResultDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(FgaChangelogConverter::toVO).toList();
    }
}
