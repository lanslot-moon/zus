package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 关系类型限制输入。
 *
 * <p>与 {@code fga_type_restriction.allowed_type + allowed_subject_relation} 对齐。
 */
@Data
public class FgaTypeRestrictionInput {

    @NotBlank(message = "restriction.type 不能为空")
    @Size(max = 64, message = "restriction.type 长度不能超过 64")
    private String type;

    @Size(max = 64, message = "restriction.relation 长度不能超过 64")
    private String relation;
}
