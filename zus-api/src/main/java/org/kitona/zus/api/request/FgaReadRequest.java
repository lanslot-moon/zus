package org.kitona.zus.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA Read API 请求
 */
@Data
public class FgaReadRequest {

    /**
     * 资源类型（可选过滤）
     */
    @Size(max = 64, message = "objectType 长度不能超过 64")
    private String objectType;

    /**
     * 资源ID（可选过滤）
     */
    @Size(max = 255, message = "objectId 长度不能超过 255")
    private String objectId;

    /**
     * 关系名称（可选过滤）
     */
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 主体类型（可选过滤）
     */
    @Size(max = 64, message = "subjectType 长度不能超过 64")
    private String subjectType;

    /**
     * 主体ID（可选过滤）
     */
    @Size(max = 255, message = "subjectId 长度不能超过 255")
    private String subjectId;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "pageSize 必须大于 0")
    private Integer pageSize;

    /**
     * 分页续传令牌
     */
    private String pageToken;
}
