package org.kitona.zus.api.request.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FGA 关系 Schema 输入。
 *
 * <p>关系名称由外层 {@code relations} Map 的 key 提供，value 只描述 rewrite 和类型限制。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaRelationSchemaInput {

    /**
     * 重写表达式，对应 OpenFGA userset rewrite。
     */
    @NotBlank(message = "relation.rewrite 不能为空")
    @Size(max = 512, message = "relation.rewrite 长度不能超过 512")
    private String rewrite;

    /**
     * 类型限制列表。
     */
    @Valid
    private List<FgaTypeRestrictionInput> allowedSubjectTypes;
}
