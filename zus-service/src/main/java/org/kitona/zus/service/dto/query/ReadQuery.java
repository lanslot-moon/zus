package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 读取元组查询
 *
 * <p>用于 Read API 的查询条件，支持多维度过滤和分页。
 * <p>所有过滤条件均为可选，传入则进行过滤，不传则不过滤该维度。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * 资源类型过滤（可选）
     */
    private String objectType;

    /**
     * 资源ID过滤（可选）
     */
    private String objectId;

    /**
     * 关系名称过滤（可选）
     */
    private String relation;

    /**
     * 主体类型过滤（可选）
     */
    private String subjectType;

    /**
     * 主体ID过滤（可选）
     */
    private String subjectId;

    /**
     * 每页数量，默认 100，最大 1000
     */
    @Min(value = 1, message = "pageSize 最小为 1")
    @Max(value = 1000, message = "pageSize 最大为 1000")
    private Integer pageSize;

    /**
     * 分页游标，首页传 null 或不传
     */
    private String pageToken;

    /**
     * 获取有效的分页大小
     */
    public int getEffectivePageSize() {
        return (pageSize != null && pageSize > 0) ? pageSize : DEFAULT_PAGE_SIZE;
    }

    /**
     * 解析分页游标为 Long 类型
     */
    public Long parsePageTokenAsLong() {
        if (pageToken == null || pageToken.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(pageToken);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
