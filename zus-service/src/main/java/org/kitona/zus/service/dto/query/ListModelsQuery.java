package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列出授权模型查询对象
 *
 * <p>用于 ListModels API 的查询条件，支持分页和状态筛选。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListModelsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    /**
     * 每页大小，默认 20，最大 100
     */
    @Min(value = 1, message = "pageSize 最小为 1")
    @Max(value = 100, message = "pageSize 最大为 100")
    private Integer pageSize;

    /**
     * 分页游标，首页传 null 或不传
     */
    private String pageToken;

    /**
     * 模型状态筛选（可选）: 0-草稿, 1-已发布, 2-已废弃
     */
    private Integer status;

    /**
     * 获取有效的每页大小
     *
     * @return 如果未设置或小于1，返回默认值 20
     */
    public int getEffectivePageSize() {
        return (pageSize != null && pageSize > 0) ? pageSize : 20;
    }
}
