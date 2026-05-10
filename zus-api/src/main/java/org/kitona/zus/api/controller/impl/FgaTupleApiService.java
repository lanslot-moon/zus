package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.converter.FgaChangelogConverter;
import org.kitona.zus.api.converter.FgaTupleConverter;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.FgaWriteResultVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.ConditionDefinitionResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.kitona.zus.service.exception.ApplicationException;
import org.kitona.zus.common.exception.IError;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * FGA Tuple API 实现。
 *
 * <p>职责分工：
 * <ul>
 *   <li>write：解析 condition 名称 → id（依赖模型应用服务），再分派到
 *       {@link ITupleMutationApplicationService#write(String, List)} 与
 *       {@link ITupleMutationApplicationService#delete(String, List)}；事务提交后通过
 *       {@link ITupleWatchApplicationService#getCurrentZookie(String)} 读取最新 zookie。</li>
 *   <li>read：直接转发到 {@link IAuthorizationReadApplicationService#read(String, TupleReadQuery)}，
 *       返回游标分页的元组列表。</li>
 *   <li>listChanges：复用 {@link ITupleWatchApplicationService#listChanges} 的应用层能力
 *       （支持按 objectType 过滤 + zookie 游标分页）。</li>
 * </ul>
 *
 * <p>说明：condition 解析依赖当前生效模型；若请求体指定 {@code authorizationModelId}
 * 由模型应用服务通过 {@link IAuthorizationModelApplicationService#getModel} 返回对应模型。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Slf4j
@Service
public class FgaTupleApiService implements IFgaTupleApiService {

    @Resource
    private ITupleMutationApplicationService tupleMutationApplicationService;

    @Resource
    private IAuthorizationReadApplicationService authorizationReadApplicationService;

    @Resource
    private ITupleWatchApplicationService tupleWatchApplicationService;

    @Resource
    private IAuthorizationModelApplicationService authorizationModelApplicationService;


    @Override
    public RestResult<FgaWriteResultVO> write(String storeId, FgaWriteRequest request) {
        Function<String, Long> conditionResolver = loadConditionResolver(storeId,
                request != null ? request.getAuthorizationModelId() : null);

        List<WriteTupleCommand> writes = FgaTupleConverter.INSTANCE.toWriteCommands(request, conditionResolver);
        List<WriteTupleCommand> deletes = FgaTupleConverter.INSTANCE.toDeleteCommands(request);

        int writtenCount = writes != null ? writes.size() : 0;
        int deletedCount = deletes != null ? deletes.size() : 0;

        if (writtenCount > 0) {
            tupleMutationApplicationService.write(storeId, writes);
        }
        if (deletedCount > 0) {
            tupleMutationApplicationService.delete(storeId, deletes);
        }

        long zookie = tupleWatchApplicationService.getCurrentZookie(storeId);
        log.info("FgaTupleApiService.write storeId={}, written={}, deleted={}, zookie={}",
                storeId, writtenCount, deletedCount, zookie);
        return RestResult.success(FgaWriteResultVO.builder()
                .zookie(zookie > 0 ? String.valueOf(zookie) : null)
                .writtenCount(writtenCount)
                .deletedCount(deletedCount)
                .build());
    }

    @Override
    public RestResult<PageResponseVO<FgaTupleVO>> read(String storeId, FgaReadRequest request) {
        TupleReadQuery query = FgaTupleConverter.INSTANCE.toReadQuery(request);
        PageResultDTO<TupleResultDTO> result = authorizationReadApplicationService.read(storeId, query);
        if (result == null || result.isEmpty()) {
            return RestResult.success(PageResponseVO.empty());
        }
        List<FgaTupleVO> voList = FgaTupleConverter.INSTANCE.toVOList(result.getData());
        return RestResult.success(PageResponseVO.of(voList, result.getContinuationToken(), result.isHasMore()));
    }

    @Override
    public RestResult<PageResponseVO<FgaTupleChangeVO>> listChanges(String storeId, String type,
                                                                    String startToken, Integer pageSize) {
        int size = pageSize != null ? pageSize : 100;
        PageResultDTO<TupleChangeResultDTO> result = tupleWatchApplicationService.listChanges(storeId, startToken, size, type);
        if (result == null || result.isEmpty()) {
            return RestResult.success(PageResponseVO.empty());
        }
        List<FgaTupleChangeVO> voList = FgaChangelogConverter.INSTANCE.toVOList(result.getData());
        return RestResult.success(PageResponseVO.of(voList, result.getContinuationToken(), result.isHasMore()));
    }

    /**
     * 构建「条件名 → 条件定义 ID」解析函数。
     *
     * <p>如果请求未指定模型，使用 Store 当前激活模型；指定则使用对应版本。
     * 当目标模型不存在或没有条件定义时，返回始终回退为 null 的 resolver，
     * 由下游 {@code WriteTupleCommand} 的 {@code @AssertTrue} 校验拦截非法调用。
     */
    private Function<String, Long> loadConditionResolver(String storeId, String modelId) {
        AuthorizationModelResultDTO model;
        if (StringUtils.isNotBlank(modelId)) {
            model = authorizationModelApplicationService.getModel(storeId, modelId);
        } else {
            model = authorizationModelApplicationService.getCurrentModel(storeId);
        }

        if (model == null) {
            log.warn("FgaTupleApiService.loadConditionResolver 未找到可用模型: storeId={}, modelId={}", storeId, modelId);
            return name -> null;
        }

        List<ConditionDefinitionResultDTO> conditions = model.getConditionDefinitions();
        if (conditions == null || conditions.isEmpty()) {
            return name -> null;
        }

        Map<String, Long> index = new HashMap<>(conditions.size() * 2);
        for (ConditionDefinitionResultDTO c : conditions) {
            if (c != null && StringUtils.isNotBlank(c.getName())) {
                index.put(c.getName(), c.getId());
            }
        }
        return name -> {
            if (StringUtils.isBlank(name)) {
                return null;
            }
            Long id = index.get(name);
            if (id == null) {
                log.warn("FgaTupleApiService 条件在模型中不存在: storeId={}, conditionName={}", storeId, name);
                throw new ApplicationException(IError.PARAMS_EXIST_ERROR);
            }
            return id;
        };
    }

}
