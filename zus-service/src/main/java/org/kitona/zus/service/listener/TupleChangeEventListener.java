package org.kitona.zus.service.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.service.dto.response.WatchChangeResultDTO;
import org.kitona.zus.service.event.WatchEventPublisher;
import org.kitona.zus.service.event.application.TupleDeletedApplicationEvent;
import org.kitona.zus.service.event.application.TupleWrittenApplicationEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 元组变更事件监听器
 *
 * <p>监听 tuple 应用事件，
 * 执行事务提交后的异步操作：
 * <ul>
 *   <li>通过 {@link WatchEventPublisher} 推送变更给 Watch 订阅者</li>
 *   <li>可扩展：更新搜索索引、触发 Webhook 等</li>
 * </ul>
 *
 * <p>注意：Changelog 写入已在 WriteApplicationService 中同步完成，
 * 本监听器不再重复写入。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Component
public class TupleChangeEventListener {

    private static final String OPERATION_WRITE = "WRITE";
    private static final String OPERATION_DELETE = "DELETE";

    @Resource
    private WatchEventPublisher watchEventPublisher;

    /**
     * 处理元组写入事件（事务提交后执行）
     *
     * @param event 元组写入应用事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTupleWritten(TupleWrittenApplicationEvent event) {
        if (event == null || event.getTupleKeys() == null || event.getTupleKeys().isEmpty()) {
            return;
        }
        String storeId = event.getStoreId();
        String zookie = event.getZookie() != null ? String.valueOf(event.getZookie().getVersion()) : null;

        log.debug("元组写入事件处理: storeId={}, zookie={}, tupleCount={}",
                storeId, zookie, event.getTupleKeys().size());

        for (TupleKey tupleKey : event.getTupleKeys()) {
            WatchChangeResultDTO change = buildChangeDTO(tupleKey, OPERATION_WRITE, zookie);
            watchEventPublisher.publish(storeId, change);
        }
    }

    /**
     * 处理元组删除事件（事务提交后执行）
     *
     * @param event 元组删除应用事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTupleDeleted(TupleDeletedApplicationEvent event) {
        if (event == null || event.getTupleKeys() == null || event.getTupleKeys().isEmpty()) {
            return;
        }
        String storeId = event.getStoreId();
        String zookie = event.getZookie() != null ? String.valueOf(event.getZookie().getVersion()) : null;

        log.debug("元组删除事件处理: storeId={}, zookie={}, tupleCount={}",
                storeId, zookie, event.getTupleKeys().size());

        for (TupleKey tupleKey : event.getTupleKeys()) {
            WatchChangeResultDTO change = buildChangeDTO(tupleKey, OPERATION_DELETE, zookie);
            watchEventPublisher.publish(storeId, change);
        }
    }

    private WatchChangeResultDTO buildChangeDTO(TupleKey tupleKey, String operation, String zookie) {
        return WatchChangeResultDTO.builder()
                .objectType(tupleKey.getObjectType())
                .objectId(tupleKey.getObjectId())
                .relation(tupleKey.getRelation())
                .subjectType(tupleKey.getSubjectType())
                .subjectId(tupleKey.getSubjectId())
                .subjectRelation(tupleKey.getSubjectRelation())
                .operation(operation)
                .zookie(zookie)
                .build();
    }
}
