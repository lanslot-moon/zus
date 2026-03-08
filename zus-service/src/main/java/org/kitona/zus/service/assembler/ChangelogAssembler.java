package org.kitona.zus.service.assembler;

import org.kitona.zus.domain.entity.ChangelogEntity;
import org.kitona.zus.service.dto.response.WatchChangeResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * 变更日志 Assembler
 *
 * <p>负责 {@link ChangelogEntity} 与 {@link WatchChangeResultDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO 之间的转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class ChangelogAssembler {

    private ChangelogAssembler() {
    }

    /**
     * 将变更日志实体转换为 DTO
     *
     * @param entity 变更日志实体
     * @return Watch 变更结果 DTO，entity 为 null 时返回 null
     */
    public static WatchChangeResultDTO toDTO(ChangelogEntity entity) {
        if (entity == null) {
            return null;
        }
        return WatchChangeResultDTO.builder()
                .zookie(entity.getZookie() != null ? entity.getZookie().toString() : null)
                .operation(entity.getOperation())
                .objectType(entity.getObjectType())
                .objectId(entity.getObjectId())
                .relation(entity.getRelation())
                .subjectType(entity.getSubjectType())
                .subjectId(entity.getSubjectId())
                .subjectRelation(entity.getSubjectRelation())
                .build();
    }

    /**
     * 批量将变更日志实体转换为 DTO
     *
     * @param entities 变更日志实体列表
     * @return DTO 列表，entities 为 null 或空时返回空列表
     */
    public static List<WatchChangeResultDTO> toDTOList(List<ChangelogEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(ChangelogAssembler::toDTO)
                .toList();
    }
}
