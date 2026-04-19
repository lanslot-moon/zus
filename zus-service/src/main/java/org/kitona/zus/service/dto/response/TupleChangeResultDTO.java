package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Watch 变更事件结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleChangeResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String zookie;
    private String operation;
    private String objectType;
    private String objectId;
    private String relation;
    private String subjectType;
    private String subjectId;
    private String subjectRelation;
    private String operatorId;
    private String requestId;
    private String source;

    /**
     * 操作时间（毫秒时间戳）
     */
    private Long operationTime;
}
