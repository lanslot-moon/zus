package org.kitona.zus.api.request;

import lombok.Data;

/**
 * FGA Read API 请求
 */
@Data
public class FgaReadRequest {

    /**
     * 资源类型（可选过滤）
     */
    private String objectType;

    /**
     * 资源ID（可选过滤）
     */
    private String objectId;

    /**
     * 关系名称（可选过滤）
     */
    private String relation;

    /**
     * 主体类型（可选过滤）
     */
    private String subjectType;

    /**
     * 主体ID（可选过滤）
     */
    private String subjectId;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 分页续传令牌
     */
    private String pageToken;
}
