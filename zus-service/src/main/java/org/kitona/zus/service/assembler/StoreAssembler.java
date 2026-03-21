package org.kitona.zus.service.assembler;

import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.service.dto.response.StoreResultDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Store 聚合根 Assembler
 *
 * <p>负责 {@link StoreAggregate} 与 {@link StoreResultDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO 之间的双向转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class StoreAssembler {

    private StoreAssembler() {
    }

    /**
     * 将聚合根转换为 DTO
     *
     * @param aggregate Store 聚合根
     * @return Store 结果 DTO，aggregate 为 null 时返回 null
     */
    public static StoreResultDTO toDTO(StoreAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        return StoreResultDTO.builder()
                .storeId(aggregate.getStoreId())
                .name(aggregate.getName())
                .description(aggregate.getDescription())
                .currentModelId(aggregate.getCurrentModelId())
                .currentZookie(aggregate.getCurrentZookie() != null ? aggregate.getCurrentZookie().getVersion() : null)
                .status(aggregate.getStatusCode())
                .createTime(aggregate.getCreateTime())
                .build();
    }

    /**
     * 批量将聚合根转换为 DTO
     *
     * @param aggregates Store 聚合根列表
     * @return DTO 列表，aggregates 为 null 或空时返回空列表
     */
    public static List<StoreResultDTO> toDTOList(List<StoreAggregate> aggregates) {
        if (aggregates == null || aggregates.isEmpty()) {
            return Collections.emptyList();
        }
        return aggregates.stream()
                .map(StoreAssembler::toDTO)
                .toList();
    }
}
