package org.kitona.zus.service.assembler;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关系元组 Assembler
 *
 * <p>负责 {@link RelationTuple} 与 DTO 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，用于领域对象与 DTO 之间的双向转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class TupleAssembler {

    private TupleAssembler() {
    }

    /**
     * 将领域实体转换为 DTO
     *
     * @param entity 关系元组实体
     * @return 读取结果 DTO，entity 为 null 时返回 null
     */
    public static TupleResultDTO toDTO(RelationTuple entity) {
        if (entity == null) {
            return null;
        }
        return TupleResultDTO.builder()
                .objectType(entity.getObjectType())
                .objectId(entity.getObjectId())
                .relation(entity.getRelation())
                .subjectType(entity.getSubjectType())
                .subjectId(entity.getSubjectId())
                .subjectRelation(entity.getSubjectRelation())
                .build();
    }

    /**
     * 批量将领域实体转换为 DTO
     *
     * @param entities 关系元组实体列表
     * @return DTO 列表，entities 为 null 或空时返回空列表
     */
    public static List<TupleResultDTO> toDTOList(List<RelationTuple> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(TupleAssembler::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 从元组列表提取去重的对象引用（格式：objectType:objectId）
     *
     * @param entities 关系元组实体列表
     * @return 去重的对象引用列表
     */
    public static List<String> toDistinctObjectRefs(List<RelationTuple> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> objects = new LinkedHashSet<>();
        for (RelationTuple entity : entities) {
            objects.add(entity.getObjectType() + ":" + entity.getObjectId());
        }
        return List.copyOf(objects);
    }

    /**
     * 从元组列表提取去重的用户信息
     *
     * @param entities 关系元组实体列表
     * @return 去重的用户 DTO 列表
     */
    public static List<ListUsersResultDTO.UserDTO> toDistinctUserDTOList(List<RelationTuple> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(TupleAssembler::toUserDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    private static ListUsersResultDTO.UserDTO toUserDTO(RelationTuple entity) {
        return ListUsersResultDTO.UserDTO.builder()
                .type(entity.getSubjectType())
                .id(entity.getSubjectId())
                .relation(entity.getSubjectRelation())
                .build();
    }
}
