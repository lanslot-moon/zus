package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.api.controller.IFgaModelApiService;
import org.kitona.zus.api.converter.FgaModelConverter;
import org.kitona.zus.api.request.FgaConditionDefinitionInput;
import org.kitona.zus.api.request.FgaCreateModelRequest;
import org.kitona.zus.api.request.FgaRelationDefinitionInput;
import org.kitona.zus.api.request.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.FgaTypeDefinitionInput;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaRelationVO;
import org.kitona.zus.api.response.FgaTypeDefinitionVO;
import org.kitona.zus.api.response.FgaTypeRestrictionVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA 授权模型 API 实现
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class FgaModelApiService implements IFgaModelApiService {

    @Resource
    private IAuthorizationModelApplicationService modelApplicationService;

    @Override
    public RestResult<FgaModelVO> createModel(String storeId, FgaCreateModelRequest request) {
        if (request == null) {
            return RestResult.fail("请求不能为空");
        }
        log.info("FgaModelApiService createModel, storeId:{}", storeId);
        CreateModelCommand command = CreateModelCommand.builder()
                .storeId(storeId)
                .schemaVersion(request.getSchemaVersion())
                .description(request.getDescription())
                .typeDefinitions(convertTypeDefinitions(request.getTypes()))
                .conditions(convertConditions(request.getConditions()))
                .build();

        AuthorizationModelResultDTO model = modelApplicationService.createModel(command);
        return RestResult.success(FgaModelConverter.toVO(model));
    }

    private List<CreateModelCommand.TypeDefinitionInput> convertTypeDefinitions(List<FgaTypeDefinitionInput> inputList) {
        if (CollectionUtils.isEmpty(inputList)) {
            return List.of();
        }
        return inputList.stream()
                .map(input -> CreateModelCommand.TypeDefinitionInput.builder()
                        .type(input.getType())
                        .relations(convertRelations(input.getRelations()))
                        .build())
                .toList();
    }

    private List<CreateModelCommand.RelationInput> convertRelations(List<FgaRelationDefinitionInput> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return List.of();
        }
        return relations.stream()
                .map(relation -> CreateModelCommand.RelationInput.builder()
                        .relationName(relation.getName())
                        .rewriteExpression(relation.getRewriteExpression())
                        .allowedSubjectTypes(flattenRestrictions(relation.getRestrictions()))
                        .build())
                .toList();
    }

    private List<CreateModelCommand.ConditionDefinitionInput> convertConditions(List<FgaConditionDefinitionInput> conditions) {
        if (CollectionUtils.isEmpty(conditions)) {
            return List.of();
        }
        return conditions.stream()
                .map(condition -> CreateModelCommand.ConditionDefinitionInput.builder()
                        .name(condition.getName())
                        .expression(condition.getExpression())
                        .parameterSchema(condition.getParameterSchema() == null
                                ? null
                                : JacksonUtil.toJSONString(condition.getParameterSchema()))
                        .description(condition.getDescription())
                        .build())
                .toList();
    }

    private List<String> flattenRestrictions(List<FgaTypeRestrictionInput> restrictions) {
        if (CollectionUtils.isEmpty(restrictions)) {
            return List.of();
        }
        return restrictions.stream()
                .map(this::toRestrictionValue)
                .filter(value -> value != null && !value.isBlank())
                .toList();
    }

    private String toRestrictionValue(FgaTypeRestrictionInput restriction) {
        if (restriction == null || restriction.getType() == null || restriction.getType().isBlank()) {
            return null;
        }
        if (restriction.getRelation() == null || restriction.getRelation().isBlank()) {
            return restriction.getType();
        }
        return restriction.getType() + "#" + restriction.getRelation();
    }

    @Override
    public RestResult<FgaModelVO> getModel(String storeId, String modelId) {
        log.debug("FgaModelApiService getModel, storeId:{}, modelId:{}", storeId, modelId);

        AuthorizationModelResultDTO model = modelApplicationService.getModel(storeId, modelId);
        log.info("FgaModelApiService.getModel 获取响应结果为:{}", JacksonUtil.toJSONString(model));
        return RestResult.success(FgaModelConverter.toVO(model));
    }

    @Override
    public RestResult<PageResponseVO<FgaModelVO>> listModels(String storeId, Integer pageSize, String pageToken, Integer status) {
        log.debug("FgaModelApiService listModels, storeId:{}, pageSize:{}, status:{}", storeId, pageSize, status);

        ListModelsQuery query = ListModelsQuery.builder()
                .storeId(storeId)
                .pageSize(pageSize)
                .pageToken(pageToken)
                .status(status)
                .build();
        PageResultDTO<AuthorizationModelResultDTO> result = modelApplicationService.listModels(query);
        List<FgaModelVO> voList = FgaModelConverter.toVOList(result.getData());
        return RestResult.success(PageResponseVO.of(voList, result.getContinuationToken(), result.isHasMore()));
    }

    @Override
    public RestResult<Boolean> publishModel(String storeId, String modelId) {
        log.info("发布授权模型: storeId={}, modelId={}", storeId, modelId);
        boolean result = modelApplicationService.publishModel(storeId, modelId);
        return RestResult.success(result);
    }

    @Override
    public RestResult<Boolean> activateModel(String storeId, String modelId) {
        log.info("激活授权模型: storeId={}, modelId={}", storeId, modelId);
        boolean result = modelApplicationService.activateModel(storeId, modelId);
        return RestResult.success(result);
    }

    @Override
    public RestResult<Boolean> deprecateModel(String storeId, String modelId) {
        log.info("废弃授权模型: storeId={}, modelId={}", storeId, modelId);
        boolean result = modelApplicationService.deprecateModel(storeId, modelId);
        return RestResult.success(result);
    }

    @Override
    public RestResult<Void> deleteModel(String storeId, String modelId) {
        log.info("FgaModelApiService deleteModel, storeId:{}, modelId:{}", storeId, modelId);
        modelApplicationService.deleteModel(storeId, modelId);
        return RestResult.success(null);
    }
}
