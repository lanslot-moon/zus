package org.kitona.zus.service.conv.assembler;

import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.service.dto.response.StoreResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * Store 读侧视图 Assembler
 *
 * <p>负责 {@link StoreView} 与 {@link StoreResultDTO} 之间的转换。
 * <p>Store 对外返回的查询结果基于读侧视图，而不是直接暴露聚合状态。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class StoreAssembler {

    private StoreAssembler() {
    }

    /**
     * 将读侧视图转换为 DTO
     *
     * @param view Store 读侧视图
     * @return Store 结果 DTO，view 为 null 时返回 null
     */
    public static StoreResultDTO toDTO(StoreView view) {
        if (view == null) {
            return null;
        }
        return StoreResultDTO.builder()
                .storeId(view.storeId())
                .name(view.name())
                .description(view.description())
                .currentModelId(view.currentModelId())
                .currentZookie(view.currentZookie())
                .status(view.status())
                .createTime(view.createTime())
                .build();
    }

    /**
     * 批量将读侧视图转换为 DTO
     *
     * @param views Store 读侧视图列表
     * @return DTO 列表，views 为 null 或空时返回空列表
     */
    public static List<StoreResultDTO> toDTOViewList(List<StoreView> views) {
        if (views == null || views.isEmpty()) {
            return Collections.emptyList();
        }
        return views.stream()
                .map(StoreAssembler::toDTO)
                .toList();
    }
}
