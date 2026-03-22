package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Store 应用层结果 DTO
 *
 * <p>用于 Store 创建/查询/列表的返回，供 API 层转换为 FgaStoreVO。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间唯一标识
     */
    private String storeId;

    /**
     * 存储空间名称
     */
    private String name;

    /**
     * 存储空间描述
     */
    private String description;

    /**
     * 当前生效的授权模型ID
     */
    private String currentModelId;

    /**
     * 当前 Zookie 版本号（读侧视图字段）
     */
    private Long currentZookie;

    /**
     * 存储空间状态
     * <p>0: 正常, 1: 禁用
     */
    private Integer status;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;
}
