package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * FGA Read API 请求
 */
@Data
public class FgaReadRequest {

    @Valid
    private FgaObjectFilterRequest object;

    /**
     * 关系名称（可选过滤）
     */
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    @Valid
    private FgaSubjectFilterRequest subject;

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
