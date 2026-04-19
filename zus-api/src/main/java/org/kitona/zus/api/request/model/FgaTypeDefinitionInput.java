package org.kitona.zus.api.request.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FGA 类型定义输入 —— 对应数据库表 {@code fga_type_definition}
 *
 * <p>一个 {@code FgaTypeDefinitionInput} 表示 DSL 中一个 {@code type xxx} 声明块，
 * 包含该类型下的全部 relation 定义。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTypeDefinitionInput {

    /**
     * 类型名（如 document、folder、user、group）
     */
    @NotBlank(message = "type 不能为空")
    @Size(max = 64, message = "type 长度不能超过 64")
    private String type;

    /**
     * 关系定义列表。
     * <p>纯 subject 类型（如 user）可传空列表，但不能为 null。
     */
    @Valid
    @NotEmpty(message = "relations 不能为空；若为主体类型请至少声明一个 relation")
    private List<FgaRelationDefinitionInput> relations;
}
