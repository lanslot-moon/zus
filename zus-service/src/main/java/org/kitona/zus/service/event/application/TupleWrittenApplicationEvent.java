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

    /**
     * 创建 tuple 写入应用事件。
     *
     * @param storeId       Store 标识
     * @param tupleKeys     已写入的 tuple key 列表
     * @param zookie        本次写入产生的一致性版本
     * @param auditMetadata 审计元数据
     */
    public TupleWrittenApplicationEvent(String storeId, List<TupleKey> tupleKeys, Zookie zookie,
                                        AuditMetadata auditMetadata) {
        super(storeId);
        this.tupleKeys = tupleKeys;
        this.zookie = zookie;
        this.auditMetadata = auditMetadata != null ? auditMetadata : AuditMetadata.EMPTY;
    }
}
