package org.kitona.zus.service.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.event.ModelActivatedEvent;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 模型激活事件监听器
 *
 * <p>监听 {@link ModelActivatedEvent}，异步更新 Store 的当前模型ID。
 *
 * <p>DDD 规范：
 * <ul>
 *   <li>通过领域事件实现跨聚合的最终一致性</li>
 *   <li>每个聚合的修改在独立事务中完成</li>
 *   <li>使用 @TransactionalEventListener 确保在激活事务提交后再执行</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Component
public class ModelActivatedEventListener {

    @Resource
    private IStoreDomainRepository storeRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void handleModelActivated(ModelActivatedEvent event) {
        String storeId = event.getStoreId();
        String modelId = event.getModelId();

        log.info("处理模型激活事件: storeId={}, modelId={}, eventId={}", storeId, modelId, event.getEventId());

        try {
            boolean result = storeRepository.updateCurrentModelId(storeId, modelId);
            if (result) {
                log.info("Store 当前模型更新成功: storeId={}, modelId={}", storeId, modelId);
            } else {
                log.warn("Store 当前模型更新失败: storeId={}, modelId={}", storeId, modelId);
            }
        } catch (Exception e) {
            log.error("处理模型激活事件异常: storeId={}, modelId={}", storeId, modelId, e);
            throw e;
        }
    }
}
