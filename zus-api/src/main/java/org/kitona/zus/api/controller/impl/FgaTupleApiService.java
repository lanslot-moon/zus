package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.converter.FgaChangelogConverter;
import org.kitona.zus.api.converter.FgaTupleConverter;
import org.kitona.zus.api.request.tuple.FgaDeleteRequest;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.FgaWriteResultVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA Tuple API 实现。
 *
 * <p>职责分工：
 * <ul>
 *   <li>write：将 API 请求转换为应用命令，再分派到
 *       {@link ITupleMutationApplicationService#write(String, String, List)}；事务提交后通过
 *       {@link ITupleWatchApplicationService#getCurrentZookie(String)} 读取最新 zookie。</li>
 *   <li>delete：只处理 tuple 精确删除，分派到
 *       {@link ITupleMutationApplicationService#delete(String, List)}；事务提交后通过
 *       {@link ITupleWatchApplicationService#getCurrentZookie(String)} 读取最新 zookie。</li>
 *   <li>read：直接转发到 {@link IAuthorizationReadApplicationService#read(String, TupleReadQuery)}，
 *       返回游标分页的元组列表。</li>
 *   <li>listChanges：复用 {@link ITupleWatchApplicationService#listChanges} 的应用层能力
 *       （支持按 objectType 过滤 + zookie 游标分页）。</li>
 * </ul>
 *
 * <p>说明：conditionName 到 conditionDefinitionId 的解析属于模型约束和写入用例的一部分，
 * 由 Service 层负责处理，API 层不加载授权模型结构。
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

    @Override
    public RestResult<FgaWriteResultVO> write(String storeId, FgaWriteRequest request) {
        List<WriteTupleCommand> writes = FgaTupleConverter.INSTANCE.toWriteCommands(request);

        int writtenCount = writes != null ? writes.size() : 0;

        if (writtenCount > 0) {
            String authorizationModelId = request != null ? request.getAuthorizationModelId() : null;
            tupleMutationApplicationService.write(storeId, authorizationModelId, writes);
        }

        long zookie = tupleWatchApplicationService.getCurrentZookie(storeId);
        log.info("FgaTupleApiService.write storeId={}, written={}, zookie={}", storeId, writtenCount, zookie);
        return RestResult.success(FgaWriteResultVO.builder()
                .zookie(zookie > 0 ? String.valueOf(zookie) : null)
                .writtenCount(writtenCount)
                .deletedCount(0)
                .build());
    }

    @Override
    public RestResult<FgaWriteResultVO> delete(String storeId, FgaDeleteRequest request) {
        List<WriteTupleCommand> deletes = FgaTupleConverter.INSTANCE.toDeleteCommands(request);
        int deletedCount = deletes != null ? deletes.size() : 0;

        if (deletedCount > 0) {
            tupleMutationApplicationService.delete(storeId, deletes);
        }

        long zookie = tupleWatchApplicationService.getCurrentZookie(storeId);
        log.info("FgaTupleApiService.delete storeId={}, deleted={}, zookie={}", storeId, deletedCount, zookie);
        return RestResult.success(FgaWriteResultVO.builder()
                .zookie(zookie > 0 ? String.valueOf(zookie) : null)
                .writtenCount(0)
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

}
