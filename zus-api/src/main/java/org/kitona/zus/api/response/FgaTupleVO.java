package org.kitona.zus.api.response;

import lombok.Data;

import java.util.Map;

/**
 * FGA 元组 VO —— 对应 {@code fga_relation_tuple}
 *
 * <p>相比旧版，补齐了 Read / Watch 所需的完整字段：条件绑定、过期时间、zookie 等。
 */
@Data
public class FgaTupleVO {

    private String objectType;
    private String objectId;
    private String relation;
    private String subjectType;
    private String subjectId;
    private String subjectRelation;

    /**
     * 绑定的条件名（ABAC）
     */
    private String conditionName;

    /**
     * 绑定时固化的条件上下文
     */
    private Map<String, Object> conditionContext;

    /**
     * 过期时间（毫秒时间戳）
     */
    private Long expiresAt;

    /**
     * 写入时的 Zookie 版本
     */
    private String zookie;
}
