package org.kitona.zus.domain.authorization.audit;

import org.apache.commons.lang3.StringUtils;

/**
 * 审计元数据值对象。
 *
 * @param operatorId 操作人
 * @param requestId 请求追踪标识
 * @param source    操作来源
 */
public record AuditMetadata(String operatorId, String requestId, String source) {

    public static final AuditMetadata EMPTY = new AuditMetadata(null, null, null);

    public static AuditMetadata of(String operatorId, String requestId, String source) {
        if (StringUtils.isAllBlank(operatorId, requestId, source)) {
            return EMPTY;
        }
        return new AuditMetadata(operatorId, requestId, source);
    }

    public boolean isEmpty() {
        return StringUtils.isAllBlank(operatorId, requestId, source);
    }
}
