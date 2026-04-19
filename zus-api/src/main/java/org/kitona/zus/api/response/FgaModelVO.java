package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 授权模型 VO —— 对应数据库表 {@code fga_auth_model}
 *
 * <p>详情视图支持三种粒度（通过 Controller 的 {@code view} 参数控制）：
 * <ul>
 *   <li>{@code DSL}    — 只返回 {@code dslText}</li>
 *   <li>{@code SCHEMA} — 只返回结构化 {@code types / conditions}</li>
 *   <li>{@code FULL}   — 两者兼具（默认）</li>
 * </ul>
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
     * 模型状态：0-草稿, 1-已发布, 2-已废弃
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
     * 类型定义列表（对应 {@code fga_type_definition} + 下挂的 relation / restriction）
     */
    private List<FgaTypeDefinitionVO> types;

    /**
     * 条件定义列表（对应 {@code fga_condition_definition}）
     */
    private List<FgaConditionVO> conditions;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 发布时间（毫秒时间戳）；DRAFT 时为 {@code null}
     */
    private Long publishTime;

    /**
     * 是否为 Store 当前激活模型
     */
    private Boolean isCurrent;
}
