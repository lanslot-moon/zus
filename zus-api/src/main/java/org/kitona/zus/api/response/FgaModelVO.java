package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 授权模型 VO
 * 
 * 对应数据库表 fga_authorization_model
 */
@Data
public class FgaModelVO {

    /**
     * 模型唯一标识（ULID）
     */
    private String modelId;

    /**
     * Schema 版本，如 1.1
     */
    private String schemaVersion;

    /**
     * 原始 DSL 文本
     */
    private String dslText;

    /**
     * 模型状态: 0-草稿, 1-已发布, 2-已废弃
     */
    private Integer status;

    /**
     * 模型状态描述
     */
    private String statusDesc;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 类型定义列表（从 fga_type_definition 等表解析）
     */
    private List<FgaTypeDefinitionVO> types;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 是否为当前生效模型
     * <p>true 表示该模型是 Store 当前使用的模型
     */
    private Boolean isCurrent;
}
