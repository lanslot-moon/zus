package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.application.coordinator.TupleMutationCoordinator;
import org.kitona.zus.service.application.coordinator.TupleMutationOutcome;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.ConditionDefinitionResultDTO;
import org.kitona.zus.service.event.application.TupleDeletedApplicationEvent;
import org.kitona.zus.service.event.application.TupleWrittenApplicationEvent;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    private IAuthorizationModelApplicationService authorizationModelApplicationService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 写入write。
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

        resolveConditionReferences(storeId, authorizationModelId, writeTuple);

        // 校验每个元组命令
        writeTuple.forEach(ValidationUtil::validate);

        TupleMutationOutcome result = tupleMutationCoordinator.write(storeId, writeTuple);
        if (!result.hasWrites()) {
            return;
        }
        log.debug("TupleMutationApplicationService.write 写入变更日志(WRITE): storeId={}, count={}", storeId, result.writtenKeys().size());
        eventPublisher.publishEvent(new TupleWrittenApplicationEvent(storeId, result.writtenKeys(), result.zookie(), result.auditMetadata()));
    }

    /**
     * 删除delete。
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

    /**
     * 根据授权模型补齐 tuple 条件定义 ID。
     *
     * <p>API 层只允许表达 conditionName 与 conditionContext。条件是否存在于模型、
     * 以及它对应的 conditionDefinitionId，属于写入用例对模型约束的校验，
     * 因此统一在应用层进入领域写入前完成。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选模型版本，空值表示使用当前激活模型
     * @param commands             待写入命令
     */
    private void resolveConditionReferences(String storeId, String authorizationModelId, List<WriteTupleCommand> commands) {
        if (commands.stream().noneMatch(this::hasConditionSnapshot)) {
            return;
        }

        AuthorizationModelResultDTO model = loadAuthorizationModel(storeId, authorizationModelId);
        Map<String, ConditionDefinitionResultDTO> conditions = model != null ? model.getConditionDefinitions() : null;

        commands.stream()
                .filter(this::hasConditionSnapshot)
                .forEach(command -> command.setConditionDefinitionId(resolveConditionDefinitionId(
                        storeId, command.getConditionName(), conditions)));
    }

    /**
     * 判断写入命令是否携带 tuple 条件快照。
     *
     * @param command 写入命令
     * @return 携带条件名称或条件上下文时返回 true
     */
    private boolean hasConditionSnapshot(WriteTupleCommand command) {
        return command != null
                && (StringUtils.isNotBlank(command.getConditionName())
                || StringUtils.isNotBlank(command.getConditionContext()));
    }

    /**
     * 加载用于本次写入校验的授权模型。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选模型版本
     * @return 授权模型详情
     */
    private AuthorizationModelResultDTO loadAuthorizationModel(String storeId, String authorizationModelId) {
        AuthorizationModelResultDTO model = StringUtils.isNotBlank(authorizationModelId)
                ? authorizationModelApplicationService.getModel(storeId, authorizationModelId)
                : authorizationModelApplicationService.getCurrentModel(storeId);
        if (model == null) {
            log.warn("TupleMutationApplicationService 未找到可用于解析条件的授权模型: storeId={}, modelId={}",
                    storeId, authorizationModelId);
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }
        return model;
    }

    /**
     * 按条件名称解析模型内的条件定义 ID。
     *
     * @param storeId        Store 标识
     * @param conditionName  条件名称
     * @param conditionIndex 模型条件定义索引
     * @return 条件定义 ID
     */
    private Long resolveConditionDefinitionId(String storeId, String conditionName,
                                              Map<String, ConditionDefinitionResultDTO> conditionIndex) {
        if (StringUtils.isBlank(conditionName) || conditionIndex == null || conditionIndex.isEmpty()) {
            log.warn("TupleMutationApplicationService tuple 条件缺少有效模型定义: storeId={}, conditionName={}",
                    storeId, conditionName);
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }

        ConditionDefinitionResultDTO condition = conditionIndex.get(conditionName);
        if (condition == null || condition.getId() == null) {
            log.warn("TupleMutationApplicationService 条件在模型中不存在: storeId={}, conditionName={}",
                    storeId, conditionName);
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }
        return condition.getId();
    }
}
