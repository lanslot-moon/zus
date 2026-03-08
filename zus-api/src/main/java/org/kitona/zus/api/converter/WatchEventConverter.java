package org.kitona.zus.api.converter;

import org.kitona.zus.api.response.WatchChangeEventVO;
import org.kitona.zus.api.response.WatchTupleDataVO;
import org.kitona.zus.service.dto.response.WatchChangeResultDTO;

import java.util.Collections;
import java.util.List;

/**
 * Watch 事件转换器
 *
 * <p>负责 {@link WatchChangeResultDTO} 与 {@link WatchChangeEventVO} 之间的转换。
 * <p>DDD 规范：Converter 位于用户接口层，用于 DTO 与 VO 之间的转换。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class WatchEventConverter {

    private WatchEventConverter() {
    }

    /**
     * 将 WatchChangeResultDTO 转换为 WatchChangeEventVO
     *
     * @param dto 变更结果 DTO
     * @return 变更事件 VO，dto 为 null 时返回 null
     */
    public static WatchChangeEventVO toChangeEvent(WatchChangeResultDTO dto) {
        if (dto == null) {
            return null;
        }
        String object = buildObjectRef(dto.getObjectType(), dto.getObjectId());
        String subject = buildSubjectRef(dto.getSubjectType(), dto.getSubjectId(), dto.getSubjectRelation());
        WatchTupleDataVO tuple = new WatchTupleDataVO(object, dto.getRelation(), subject);
        return new WatchChangeEventVO(dto.getZookie(), dto.getOperation(), tuple);
    }

    /**
     * 批量转换
     *
     * @param dtoList DTO 列表
     * @return WatchChangeEventVO 列表
     */
    public static List<WatchChangeEventVO> toChangeEventList(List<WatchChangeResultDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(WatchEventConverter::toChangeEvent)
                .toList();
    }

    /**
     * 构建对象引用字符串，格式：type:id
     */
    private static String buildObjectRef(String objectType, String objectId) {
        if (objectType == null || objectId == null) {
            return "";
        }
        return objectType + ":" + objectId;
    }

    /**
     * 构建主体引用字符串，格式：type:id 或 type:id#relation
     */
    private static String buildSubjectRef(String subjectType, String subjectId, String subjectRelation) {
        if (subjectType == null || subjectId == null) {
            return "";
        }
        String base = subjectType + ":" + subjectId;
        if (subjectRelation != null && !subjectRelation.isEmpty()) {
            return base + "#" + subjectRelation;
        }
        return base;
    }
}
