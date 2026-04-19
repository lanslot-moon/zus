package org.kitona.zus.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 元组批量变更请求。
 */
@Data
public class FgaTupleMutationRequest {

    @Valid
    @NotEmpty(message = "tuples 不能为空")
    private List<FgaTupleRequest> tuples;

    @Valid
    private FgaAuditMetadataRequest audit;
}
