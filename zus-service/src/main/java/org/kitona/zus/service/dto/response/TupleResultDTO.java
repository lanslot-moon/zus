package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 关系元组结果 DTO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectType;
    private String objectId;
    private String relation;
    private String subjectType;
    private String subjectId;
    private String subjectRelation;

    /**
     * 元组写入时对应的 Zookie 版本，用于上层做一致性 token。
     */
    private String zookie;

    /**
     * 绑定的条件名称快照，未绑定条件时为 null。
     */
    private String conditionName;

    /**
     * 条件上下文快照（JSON 文本），未绑定条件时为 null。
     */
    private String conditionContext;

    /**
     * 过期时间（毫秒时间戳），null 表示永不过期。
     */
    private Long expiresAt;
}
