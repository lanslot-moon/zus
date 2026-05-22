package org.kitona.zus.service.conv.assembler;

import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.read.view.ChangelogView;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * 变更日志 Assembler
 *
 * <p>负责 {@link Changelog} 与 {@link TupleChangeResultDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO 之间的转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class ChangelogAssembler {

    /**
     * 创建 ChangelogAssembler 工具类私有构造方法，防止外部实例化。
     */
    private ChangelogAssembler() {
    }

    /**
     * 将变更日志实体转换为 DTO
     *
     * @param entity 变更日志实体
     * @return Watch 变更结果 DTO，entity 为 null 时返回 null
     */
    public static TupleChangeResultDTO toDTO(Changelog entity) {
        if (entity == null) {
            return null;
        }
        return TupleChangeResultDTO.builder()
                .zookie(entity.getZookie() != null ? entity.getZookie().toString() : null)
                .operation(entity.getOperation())
                .objectType(entity.getObjectType())
                .objectId(entity.getObjectId())
                .relation(entity.getRelation())
                .subjectType(entity.getSubjectType())
                .subjectId(entity.getSubjectId())
                .subjectRelation(entity.getSubjectRelation())
                .operatorId(entity.getOperatorId())
                .requestId(entity.getRequestId())
                .source(entity.getSource())
                .operationTime(entity.getOperationTime())
                .build();
    }

    /**
     * 将变更日志读侧视图转换为 DTO。
     *
     * @param view 变更日志读侧视图
     * @return Watch 变更结果 DTO，view 为 null 时返回 null
     */
    public static TupleChangeResultDTO toDTO(ChangelogView view) {
        if (view == null) {
            return null;
        }
        return TupleChangeResultDTO.builder()
                .zookie(view.zookie() != null ? view.zookie().toString() : null)
                .operation(view.operation())
                .objectType(view.objectType())
                .objectId(view.objectId())
                .relation(view.relation())
                .subjectType(view.subjectType())
                .subjectId(view.subjectId())
                .subjectRelation(view.subjectRelation())
                .operatorId(view.operatorId())
                .requestId(view.requestId())
                .source(view.source())
                .operationTime(view.operationTime())
                .build();
    }

    /**
     * 批量将变更日志实体转换为 DTO
     *
     * @param entities 变更日志实体列表
     * @return DTO 列表，entities 为 null 或空时返回空列表
     */
    public static List<TupleChangeResultDTO> toDTOList(List<ChangelogView> views) {
        if (views == null || views.isEmpty()) {
            return Collections.emptyList();
        }
        return views.stream()
                .map(ChangelogAssembler::toDTO)
                .toList();
    }
}
