package org.kitona.zus.service.event.application;

import lombok.Getter;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.List;

/**
 * tuple 写入应用事件
 *
 * <p>表示写事务已经成功提交了 tuple 和 changelog，监听方只能做提交后的附加动作。
 */
@Getter
public class TupleWrittenApplicationEvent extends ApplicationEvent {

    private final List<TupleKey> tupleKeys;
    private final Zookie zookie;
    private final AuditMetadata auditMetadata;

    public TupleWrittenApplicationEvent(String storeId, List<TupleKey> tupleKeys, Zookie zookie,
                                        AuditMetadata auditMetadata) {
        super(storeId);
        this.tupleKeys = tupleKeys;
        this.zookie = zookie;
        this.auditMetadata = auditMetadata != null ? auditMetadata : AuditMetadata.EMPTY;
    }
}
