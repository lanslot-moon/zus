package org.kitona.zus.api.controller.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.api.controller.IFgaTypeDefinitionApiService;
import org.kitona.zus.api.request.FgaAddRelationRequest;
import org.kitona.zus.api.request.FgaAddTypeDefinitionRequest;
import org.kitona.zus.api.response.FgaRelationVO;
import org.kitona.zus.api.response.FgaTypeDefinitionVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.service.application.IModelSchemaApplicationService;
import org.kitona.zus.service.dto.command.AddRelationCommand;
import org.kitona.zus.service.dto.command.AddTypeDefinitionCommand;
import org.kitona.zus.service.dto.response.RelationResultDTO;
import org.kitona.zus.service.dto.response.TypeDefinitionResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FGA 类型定义管理 API 实现
 *
 * <p>委托给「模型结构」应用服务 {@link IModelSchemaApplicationService}，对应领域能力：管理授权模型结构。
 *
 * @author kitona
 * @version 2.0.0
 * @since 2025-02-06
 */
@Slf4j
@Service
public class FgaTypeDefinitionApiService implements IFgaTypeDefinitionApiService {

    @Resource
    private IModelSchemaApplicationService modelSchemaApplicationService;

    // ==================== 类型定义管理 ====================

    @Override
    public RestResult<Boolean> addTypeDefinition(String storeId, String modelId, FgaAddTypeDefinitionRequest request) {
        if (request == null) {
            return RestResult.fail("请求不能为空");
        }
        log.info("FgaTypeDefinitionApiService addTypeDefinition, storeId:{}, modelId:{}, type:{}", storeId, modelId, request.getType());

        AddTypeDefinitionCommand command = MapstructUtil.convert(request, AddTypeDefinitionCommand.class);
        command.setStoreId(storeId);
        command.setModelId(modelId);

        boolean result = modelSchemaApplicationService.addTypeDefinition(command);
        return RestResult.success(result);
    }

    @Override
    public RestResult<List<FgaTypeDefinitionVO>> listTypeDefinitions(String storeId, String modelId) {
        log.debug("FgaTypeDefinitionApiService listTypeDefinitions, storeId:{}, modelId:{}", storeId, modelId);

        List<TypeDefinitionResultDTO> resultList = modelSchemaApplicationService.listTypeDefinitions(storeId, modelId);
        List<FgaTypeDefinitionVO> voList = MapstructUtil.convert(resultList, FgaTypeDefinitionVO.class);
        return RestResult.success(voList);
    }

    @Override
    public RestResult<FgaTypeDefinitionVO> getTypeDefinition(String storeId, String modelId, String type) {
        log.debug("FgaTypeDefinitionApiService getTypeDefinition, storeId:{}, modelId:{}, type:{}", storeId, modelId, type);

        TypeDefinitionResultDTO result = modelSchemaApplicationService.getTypeDefinition(storeId, modelId, type);
        FgaTypeDefinitionVO vo = MapstructUtil.convert(result, FgaTypeDefinitionVO.class);
        return RestResult.success(vo);
    }

    @Override
    public RestResult<Boolean> deleteTypeDefinition(String storeId, String modelId, String type) {
        log.info("FgaTypeDefinitionApiService deleteTypeDefinition, storeId:{}, modelId:{}, type:{}", storeId, modelId, type);

        boolean result = modelSchemaApplicationService.deleteTypeDefinition(storeId, modelId, type);
        return RestResult.success(result);
    }

    // ==================== 关系定义管理 ====================

    @Override
    public RestResult<Boolean> addRelation(String storeId, String modelId, String type, FgaAddRelationRequest request) {
        if (request == null) {
            return RestResult.fail("请求不能为空");
        }
        log.info("FgaTypeDefinitionApiService addRelation, storeId:{}, modelId:{}, type:{}, relationName:{}", 
                storeId, modelId, type, request.getRelationName());

        AddRelationCommand command = MapstructUtil.convert(request, AddRelationCommand.class);
        command.setStoreId(storeId);
        command.setModelId(modelId);
        command.setType(type);

        boolean result = modelSchemaApplicationService.addRelation(command);
        return RestResult.success(result);
    }

    @Override
    public RestResult<List<FgaRelationVO>> listRelations(String storeId, String modelId, String type) {
        log.debug("FgaTypeDefinitionApiService listRelations, storeId:{}, modelId:{}, type:{}", storeId, modelId, type);

        List<RelationResultDTO> resultList = modelSchemaApplicationService.listRelations(storeId, modelId, type);
        List<FgaRelationVO> voList = MapstructUtil.convert(resultList, FgaRelationVO.class);
        return RestResult.success(voList);
    }

    @Override
    public RestResult<FgaRelationVO> getRelation(String storeId, String modelId, String type, String relationName) {
        log.debug("FgaTypeDefinitionApiService getRelation, storeId:{}, modelId:{}, type:{}, relationName:{}", 
                storeId, modelId, type, relationName);

        RelationResultDTO result = modelSchemaApplicationService.getRelation(storeId, modelId, type, relationName);
        FgaRelationVO vo = MapstructUtil.convert(result, FgaRelationVO.class);
        return RestResult.success(vo);
    }

    @Override
    public RestResult<Boolean> updateRelation(String storeId, String modelId, String type, String relationName, FgaAddRelationRequest request) {
        if (request == null) {
            return RestResult.fail("请求不能为空");
        }
        log.info("FgaTypeDefinitionApiService updateRelation, storeId:{}, modelId:{}, type:{}, relationName:{}", 
                storeId, modelId, type, relationName);

        AddRelationCommand command = MapstructUtil.convert(request, AddRelationCommand.class);
        command.setStoreId(storeId);
        command.setModelId(modelId);
        command.setType(type);
        command.setRelationName(relationName);

        boolean result = modelSchemaApplicationService.updateRelation(command);
        return RestResult.success(result);
    }

    @Override
    public RestResult<Boolean> deleteRelation(String storeId, String modelId, String type, String relationName) {
        log.info("FgaTypeDefinitionApiService deleteRelation, storeId:{}, modelId:{}, type:{}, relationName:{}", 
                storeId, modelId, type, relationName);

        boolean result = modelSchemaApplicationService.deleteRelation(storeId, modelId, type, relationName);
        return RestResult.success(result);
    }
}
