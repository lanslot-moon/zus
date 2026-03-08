package org.kitona.zus.api.response;

import lombok.Data;

/**
 * FGA 元组 VO
 */
@Data
public class FgaTupleVO {

    /**
     * 资源类型
     */
    private String objectType;

    /**
     * 资源ID
     */
    private String objectId;

    /**
     * 关系名称
     */
    private String relation;

    /**
     * 主体类型
     */
    private String subjectType;

    /**
     * 主体ID
     */
    private String subjectId;

    /**
     * 主体关系（用户集时使用）
     */
    private String subjectRelation;
}
