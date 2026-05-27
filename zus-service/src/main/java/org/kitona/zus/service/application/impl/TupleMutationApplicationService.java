package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.application.coordinator.TupleMutationCoordinator;
import org.kitona.zus.service.application.coordinator.TupleMutationOutcome;
import org.kitona.zus.service.event.application.TupleDeletedApplicationEvent;
import org.kitona.zus.service.event.application.TupleWrittenApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Write 应用服务
 *
 * <p>
 * 提供元组变更用例的应用层实现，负责参数校验、调用协调服务并发布应用事件。
 *
 * <h3>边界说明</h3>
 * <p>
 * {@link RelationTuple} 是独立聚合根；
 * tuple、changelog 与 zookie 的组合写入属于跨聚合协调流程，
 * 不作为 Store 聚合内部行为在本类中直接建模。
 *
 * <h3>DDD 规范</h3>
 * <ul>
 * <li>应用层不直接持有底层持久化细节，由协调服务负责用例编排</li>
 * <li>事务内完成核心状态变更后，仅发布应用事件通知提交后的附加动作</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 * @see RelationTuple
 * @see Changelog
 */
@Slf4j
@Service
public class TupleMutationApplicationService implements ITupleMutationApplicationService {

    @Resource
    private TupleMutationCoordinator tupleMutationCoordinator;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 写入关系元组。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选模型版本，用于解析 tuple 条件定义
     * @param writeTuple           writeTuple 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void write(String storeId, String authorizationModelId, List<WriteTupleCommand> writeTuple) {
        if (CollectionUtils.isEmpty(writeTuple)) {
            return;
        }

        // 校验每个元组命令
        writeTuple.forEach(ValidationUtil::validate);

        TupleMutationOutcome result = tupleMutationCoordinator.write(storeId, authorizationModelId, writeTuple);
        if (!result.hasWrites()) {
            return;
        }
        log.debug("TupleMutationApplicationService.write 写入变更日志(WRITE): storeId={}, count={}", storeId, result.writtenKeys().size());
        eventPublisher.publishEvent(new TupleWrittenApplicationEvent(storeId, result.writtenKeys(), result.zookie(), result.auditMetadata()));
    }

    /**
     * 删除关系元组。
     *
     * @param storeId Store 标识
     * @param deleteTuple deleteTuple 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String storeId, List<WriteTupleCommand> deleteTuple) {
        if (CollectionUtils.isEmpty(deleteTuple)) {
            return;
        }

        // 校验每个元组命令
        deleteTuple.forEach(ValidationUtil::validate);

        TupleMutationOutcome result = tupleMutationCoordinator.delete(storeId, deleteTuple);
        if (!result.hasDeletes()) {
            return;
        }
        log.debug("TupleMutationApplicationService.delete 写入变更日志(DELETE): storeId={}, count={}", storeId, result.deletedKeys().size());
        eventPublisher.publishEvent(new TupleDeletedApplicationEvent(storeId, result.deletedKeys(), result.zookie(), result.auditMetadata()));
    }

}
