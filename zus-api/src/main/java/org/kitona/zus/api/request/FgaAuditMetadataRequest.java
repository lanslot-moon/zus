package org.kitona.zus.api.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审计元数据请求。
 */
@Data
public class FgaAuditMetadataRequest {

    @Size(max = 128, message = "operatorId 长度不能超过 128")
    private String operatorId;

    @Size(max = 64, message = "requestId 长度不能超过 64")
    private String requestId;

    @Size(max = 32, message = "source 长度不能超过 32")
    private String source;
}
