package org.kitona.zus.api.response;

import lombok.Data;

/**
 * FGA 存储空间 VO
 */
@Data
public class FgaStoreVO {

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
     * 当前 Zookie 版本号
     */
    private Long currentZookie;

    /**
     * 存储空间状态
     * <p>0: 正常, 1: 禁用
     */
    private Integer status;
}
