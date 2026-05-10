package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.converter.FgaModelConverter;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA Authorization Model API 实现。
 *
 * <p>职责：
 * <ul>
 *   <li>请求参数校验 / 视图裁剪（DSL / SCHEMA / FULL）；</li>
 *   <li>调用 {@link IAuthorizationModelApplicationService} 应用服务；</li>
 *   <li>通过 {@link FgaModelConverter} 完成 Request ↔ Command、DTO ↔ VO 的转换；</li>
 *   <li>不直接参与业务状态校验（由应用服务抛 {@code ApplicationException}，由全局异常处理器映射为错误响应）。</li>
 * </ul>
 *
 * <p>DSL 模式暂不支持，本实现在收到 {@code dslText} 时直接返回 501 错误，
 * 待 DSL 解析器完成后在应用服务内部打开支持。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Slf4j
@Service
public class FgaAuthorizationModelApiService implements IFgaAuthModelApiService {

    @Resource
    private IAuthorizationModelApplicationService modelApplicationService;

    @Override
    public RestResult<FgaModelVO> writeModel(String storeId, FgaWriteAuthorizationModelRequest request) {
        if (request != null && StringUtils.isNotBlank(request.getDslText())) {
            log.warn("FgaAuthorizationModelApiService.writeModel 暂未支持 DSL 模式: storeId={}", storeId);
            return RestResult.error(501, "暂未支持 DSL 文本模式，请使用 Schema 模式传入 typeDefinitions");
        }

        CreateModelCommand command = FgaModelConverter.INSTANCE.toCreateModelCommand(storeId, request);
        AuthorizationModelResultDTO created = modelApplicationService.createModel(command);
        log.info("FgaAuthorizationModelApiService.writeModel 成功: storeId={}, result={}", storeId, JacksonUtil.toJSONString(created));
        return RestResult.success(FgaModelConverter.INSTANCE.toVO(created));
    }

    @Override
    public RestResult<Void> publishModel(String storeId, String modelId) {
        log.info("FgaAuthorizationModelApiService.publishModel storeId={}, modelId={}", storeId, modelId);
        boolean ok = modelApplicationService.publishModel(storeId, modelId);
        return ok ? RestResult.success(null) : RestResult.fail("发布模型失败");
    }

    @Override
    public RestResult<Void> activateModel(String storeId, String modelId) {
        log.info("FgaAuthorizationModelApiService.activateModel storeId={}, modelId={}", storeId, modelId);
        boolean ok = modelApplicationService.activateModel(storeId, modelId);
        return ok ? RestResult.success(null) : RestResult.fail("激活模型失败");
    }

    @Override
    public RestResult<Void> deprecateModel(String storeId, String modelId) {
        log.info("FgaAuthorizationModelApiService.deprecateModel storeId={}, modelId={}", storeId, modelId);
        boolean ok = modelApplicationService.deprecateModel(storeId, modelId);
        return ok ? RestResult.success(null) : RestResult.fail("废弃模型失败");
    }

    @Override
    public RestResult<Void> deleteModel(String storeId, String modelId) {
        log.info("FgaAuthorizationModelApiService.deleteModel storeId={}, modelId={}", storeId, modelId);
        boolean ok = modelApplicationService.deleteModel(storeId, modelId);
        return ok ? RestResult.success(null) : RestResult.fail("删除模型失败");
    }

    @Override
    public RestResult<FgaModelVO> getCurrentModel(String storeId, String view) {
        log.info("FgaAuthorizationModelApiService.getCurrentModel storeId={}, view={}", storeId, view);
        AuthorizationModelResultDTO dto = modelApplicationService.getCurrentModel(storeId);
        if (dto == null) {
            return RestResult.success(null);
        }
        return RestResult.success(FgaModelConverter.INSTANCE.toVO(dto));
    }

    @Override
    public RestResult<FgaModelVO> getModel(String storeId, String modelId, String view) {
        log.info("FgaAuthorizationModelApiService.getModel storeId={}, modelId={}, view={}", storeId, modelId, view);
        AuthorizationModelResultDTO dto = modelApplicationService.getModel(storeId, modelId);
        return RestResult.success(FgaModelConverter.INSTANCE.toVO(dto));
    }

    @Override
    public RestResult<PageResponseVO<FgaModelVO>> listModels(String storeId, Integer pageSize, String pageToken, Integer status) {
        log.info("FgaAuthorizationModelApiService.listModels storeId={}, pageSize={}, pageToken={}, status={}",
                storeId, pageSize, pageToken, status);
        ListModelsQuery query = FgaModelConverter.INSTANCE.toListModelsQuery(storeId, status, pageSize, pageToken);
        PageResultDTO<AuthorizationModelResultDTO> result = modelApplicationService.listModels(query);
        List<FgaModelVO> voList = FgaModelConverter.INSTANCE.toVOList(result.getData());
        return RestResult.success(PageResponseVO.of(voList, result.getContinuationToken(), result.isHasMore()));
    }
}
