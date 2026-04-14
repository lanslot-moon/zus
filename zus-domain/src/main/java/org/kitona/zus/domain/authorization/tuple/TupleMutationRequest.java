package org.kitona.zus.domain.authorization.tuple;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import java.util.Objects;

/**
 * tuple 变更请求。
 *
 * <p>封装写入/删除 tuple 所需的领域信息。
 */
public record TupleMutationRequest(
        TupleKey tupleKey,
        TupleCondition condition,
        Long expiresAt,
        AuditMetadata auditMetadata
) {

    public TupleMutationRequest {
        tupleKey = Objects.requireNonNull(tupleKey, "tupleKey 不能为空");
        condition = condition != null ? condition : TupleCondition.EMPTY;
        auditMetadata = auditMetadata != null ? auditMetadata : AuditMetadata.EMPTY;
    }

    public static TupleMutationRequest of(TupleKey tupleKey, TupleCondition condition) {
        return new TupleMutationRequest(tupleKey, condition, null, AuditMetadata.EMPTY);
    }

    public static TupleMutationRequest of(TupleKey tupleKey, TupleCondition condition,
                                          Long expiresAt, AuditMetadata auditMetadata) {
        return new TupleMutationRequest(tupleKey, condition, expiresAt, auditMetadata);
    }
}
