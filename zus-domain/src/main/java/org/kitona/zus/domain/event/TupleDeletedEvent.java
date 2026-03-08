package org.kitona.zus.domain.event;

import lombok.Getter;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.List;

/**
 * 元组删除事件
 * 
 * 当元组被删除时触发此事件。
 * 用于通知相关组件进行缓存失效等操作。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public class TupleDeletedEvent extends DomainEvent {

    /**
     * 事件类型常量
     */
    public static final String EVENT_TYPE = "tuple.deleted";

    /**
     * 删除的元组键列表
     */
    private final List<TupleKey> tupleKeys;

    /**
     * 删除后的 Zookie
     */
    private final Zookie zookie;

    /**
     * 构造函数
     * 
     * @param storeId   存储空间ID
     * @param tupleKeys 元组键列表
     * @param zookie    删除后的 Zookie
     */
    public TupleDeletedEvent(String storeId, List<TupleKey> tupleKeys, Zookie zookie) {
        super(storeId);
        this.tupleKeys = tupleKeys;
        this.zookie = zookie;
    }

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }
}
