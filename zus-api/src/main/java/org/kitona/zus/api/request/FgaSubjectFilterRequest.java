package org.kitona.zus.api.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 主体过滤条件。
 */
@Data
public class FgaSubjectFilterRequest {

    @Size(max = 64, message = "subject.type 长度不能超过 64")
    private String type;

    @Size(max = 128, message = "subject.id 长度不能超过 128")
    private String id;

    @Size(max = 64, message = "subject.relation 长度不能超过 64")
    private String relation;
}
