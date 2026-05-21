package org.kitona.zus.api.request.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 写授权模型请求 —— 统一「创建 / 编辑草稿」入口
 *
 * <p>对应数据库表：{@code fga_auth_model} / {@code fga_type_definition}
 * / {@code fga_relation_definition} / {@code fga_type_restriction}
 * / {@code fga_condition_definition}。
 *
 * <h3>两种输入模式，二选一：</h3>
 * <ol>
 *   <li><b>DSL 模式</b>：仅填 {@code dslText}，服务端解析为结构化并入库。
 *       适合 CI / 开发者直接粘贴 {@code .fga} 文件。</li>
 *   <li><b>Schema 模式</b>：填 {@code schemaVersion + types + conditions}，
 *       不经 DSL 解析直接入库。适合 UI 可视化编辑器或程序化生成场景。</li>
 * </ol>
 *
 * <p>服务端校验规则：{@code dslText} 与 {@code types} 不能同时为空，且互斥。
 *
 * <h3>创建后语义</h3>
 * 模型默认 {@code status = DRAFT(0)}，需显式 {@code publish} 才可用于 Check，
 * 再 {@code activate} 才会被 Store 采用为 {@code current_model_id}。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaWriteAuthorizationModelRequest {

    /**
     * 原始 DSL 文本（OpenFGA DSL 方言）。
     * <p>与 {@link #types} 二选一。
     */
    private String dslText;

    /**
     * Schema 版本号，默认 {@code 1.1}（仅 Schema 模式生效；DSL 模式从首行注释解析）
     */
    @Size(max = 16, message = "schemaVersion 长度不能超过 16")
    private String schemaVersion;

    /**
     * 结构化类型定义映射。
     * <p>与 {@link #dslText} 二选一。
     * <p>key 为 type name，value 不再重复携带 type 名称。
     */
    @Valid
    private Map<String, FgaTypeSchemaInput> types;

    /**
     * 条件定义映射（ABAC 混合模式）。
     * <p>key 为 condition name，value 不再重复携带 condition 名称。
     */
    @Valid
    private Map<String, FgaConditionSchemaInput> conditions;

    /**
     * 模型描述
     */
    @Size(max = 512, message = "description 长度不能超过 512")
    private String description;
}
