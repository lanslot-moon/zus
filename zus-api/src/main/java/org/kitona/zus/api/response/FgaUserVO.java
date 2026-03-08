package org.kitona.zus.api.response;

import lombok.Data;

/**
 * FGA 用户 VO
 */
@Data
public class FgaUserVO {

    /**
     * 主体类型，如 user
     */
    private String type;

    /**
     * 主体ID
     */
    private String id;

    /**
     * 主体关系（用户集时使用）
     */
    private String relation;
}
