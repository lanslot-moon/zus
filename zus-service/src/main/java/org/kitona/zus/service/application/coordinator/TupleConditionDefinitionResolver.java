package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * tuple 条件定义解析器。
 *
 * <p>该组件属于应用层模型查询与命令转换辅助能力，负责把写入命令中的
 * conditionName 解析为领域 tuple 需要的 conditionDefinitionId。
 * 它直接读取领域模型聚合，不通过模型应用服务 DTO，避免用例之间相互调用。
 */
@Component
public class TupleConditionDefinitionResolver {

    /**
     * Store 查询端口，用于未显式指定模型时定位当前模型。
     */
    @Resource
    private IStoreQueryPort storeQueryRepository;

    /**
     * 授权模型聚合仓储，用于读取条件定义结构。
     */
    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    /**
     * 批量解析写入命令中引用的条件定义。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @param commands             写入命令列表
     * @return 条件名称到条件定义 ID 的映射；没有条件引用时返回空映射
     */
    Map<String, Long> resolve(String storeId, String authorizationModelId, List<WriteTupleCommand> commands) {
        Set<String> conditionNames = commands.stream()
                .filter(command -> command != null && StringUtils.isNotBlank(command.getConditionName()))
                .map(WriteTupleCommand::getConditionName)
                .collect(Collectors.toSet());
        if (conditionNames.isEmpty()) {
            return Map.of();
        }

        AuthorizationModelAggregate model = loadModel(storeId, authorizationModelId);
        Map<String, ConditionDefinition> conditionIndex = model.getConditionDefinitions().stream()
                .collect(Collectors.toMap(ConditionDefinition::getConditionName, Function.identity()));

        return conditionNames.stream()
                .collect(Collectors.toMap(Function.identity(), conditionName ->
                        resolveConditionDefinitionId(conditionName, conditionIndex)));
    }

    /**
     * 判断命令是否携带条件快照。
     *
     * @param command 写入命令
     * @return 携带条件名称、条件上下文或条件定义 ID 时返回 {@code true}
     */
    boolean hasConditionSnapshot(WriteTupleCommand command) {
        return command != null
                && (StringUtils.isNotBlank(command.getConditionName())
                || StringUtils.isNotBlank(command.getConditionContext())
                || command.getConditionDefinitionId() != null);
    }

    /**
     * 解析单条命令最终应写入领域 tuple 的条件定义 ID。
     *
     * @param command              写入命令
     * @param resolvedConditionIds 条件名称到条件定义 ID 的映射
     * @return 条件定义 ID；无条件时返回 {@code null}
     */
    Long resolveConditionDefinitionId(WriteTupleCommand command, Map<String, Long> resolvedConditionIds) {
        if (!hasConditionSnapshot(command)) {
            return null;
        }
        if (StringUtils.isNotBlank(command.getConditionName())) {
            return resolvedConditionIds.get(command.getConditionName());
        }
        if (command.getConditionDefinitionId() != null) {
            return command.getConditionDefinitionId();
        }
        throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
    }

    /**
     * 加载用于条件解析的授权模型聚合。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @return 授权模型聚合
     */
    private AuthorizationModelAggregate loadModel(String storeId, String authorizationModelId) {
        String modelId = resolveModelId(storeId, authorizationModelId);
        Optional<AuthorizationModelAggregate> optional = modelRepository.findById(AuthorizationModelId.of(storeId, modelId));
        return optional.orElseThrow(() -> new ApplicationException(IError.PARAMS_EXIST_ERROR));
    }

    /**
     * 解析本次写入需要使用的授权模型 ID。
     *
     * @param storeId              Store 标识
     * @param authorizationModelId 可选授权模型标识
     * @return 授权模型标识
     */
    private String resolveModelId(String storeId, String authorizationModelId) {
        if (StringUtils.isNotBlank(authorizationModelId)) {
            return authorizationModelId;
        }
        StoreView storeView = storeQueryRepository.findViewByStoreId(storeId)
                .orElseThrow(() -> new ApplicationException(IError.DATA_NOT_EXIST));
        if (StringUtils.isBlank(storeView.currentModelId())) {
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }
        return storeView.currentModelId();
    }

    /**
     * 按条件名称解析条件定义 ID。
     *
     * @param conditionName  条件名称
     * @param conditionIndex 条件定义索引
     * @return 条件定义 ID
     */
    private Long resolveConditionDefinitionId(String conditionName, Map<String, ConditionDefinition> conditionIndex) {
        ConditionDefinition condition = conditionIndex.get(conditionName);
        if (condition == null) {
            throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
        }
        return condition.getId();
    }
}
