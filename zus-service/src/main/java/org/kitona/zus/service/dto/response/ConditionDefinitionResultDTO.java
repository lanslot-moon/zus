package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 条件定义结果 DTO。
 *
 * <p>对应数据库表 {@code fga_condition_definition}。
 * 用于 ABAC 混合模式，携带 CEL 表达式、参数结构等条件元数据。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionDefinitionResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 条件定义 ID（数据库主键）
     */
    private Long id;

    /**
     * 条件名（模型内唯一）
     */
    private String name;

    /**
     * CEL 表达式
     */
    private String expression;

    /**
     * 参数结构（原始存储形式，通常为 JSON 字符串）
     */
    private String parameterSchema;

    /**
     * 条件描述
     */
    private String description;
}
