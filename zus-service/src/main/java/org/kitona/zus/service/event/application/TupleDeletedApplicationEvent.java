package org.kitona.zus.service.event.application;

import lombok.Getter;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.List;

/**
 * tuple 删除应用事件
 *
 * <p>表示删除事务已经成功提交了真实发生的删除和对应 changelog。
 */
@Getter
public class TupleDeletedApplicationEvent extends ApplicationEvent {

    private final List<TupleKey> tupleKeys;
    private final Zookie zookie;
    private final AuditMetadata auditMetadata;

    /**
     * 创建 tuple 删除应用事件。
     *
     * @param storeId       Store 标识
     * @param tupleKeys     已删除的 tuple key 列表
     * @param zookie        本次删除产生的一致性版本
     * @param auditMetadata 审计元数据
     */
    public TupleDeletedApplicationEvent(String storeId, List<TupleKey> tupleKeys, Zookie zookie,
                                        AuditMetadata auditMetadata) {
        super(storeId);
        this.tupleKeys = tupleKeys;
        this.zookie = zookie;
        this.auditMetadata = auditMetadata != null ? auditMetadata : AuditMetadata.EMPTY;
    }
}
