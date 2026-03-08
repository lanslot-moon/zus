package org.kitona.zus.api.controller;

import jakarta.validation.Valid;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.api.request.FgaAddRelationRequest;
import org.kitona.zus.api.request.FgaAddTypeDefinitionRequest;
import org.kitona.zus.api.response.FgaRelationVO;
import org.kitona.zus.api.response.FgaTypeDefinitionVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FGA 类型定义管理 API
 *
 * 用于对授权模型中的类型定义（TypeDefinition）和关系（Relation）进行增量管理。
 * 支持在草稿状态的模型上添加、更新、删除类型定义和关系。
 *
 * 对应数据库表：
 * - fga_type_definition: 类型定义表
 * - fga_model_relation: 关系定义表
 * - fga_relation_restriction: 关系类型限制表
 *
 * 注意：只有草稿状态(status=0)的模型才能进行类型和关系的增删改操作。
 */
@RestController
@RequestMapping("/fga/stores/{storeId}/authorization-models/{modelId}")
public interface IFgaTypeDefinitionApiService {

    // ==================== 类型定义管理 ====================

    /**
     * 添加类型定义
     *
     * 向指定模型添加一个新的类型定义（如 document、folder）。
     * 只能对草稿状态的模型进行操作。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param request 类型定义请求，包含类型名、关系定义、关系限制
     * @return 创建结果
     */
    @PostMapping("/type-definitions")
    RestResult<Boolean> addTypeDefinition(@PathVariable String storeId, @PathVariable String modelId,
                                          @Valid @RequestBody FgaAddTypeDefinitionRequest request);

    /**
     * 获取类型定义列表
     *
     * 获取指定模型下所有类型定义。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 类型定义列表
     */
    @GetMapping("/type-definitions")
    RestResult<List<FgaTypeDefinitionVO>> listTypeDefinitions(@PathVariable String storeId, @PathVariable String modelId);

    /**
     * 获取指定类型定义
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名，如 document
     * @return 类型定义详情
     */
    @GetMapping("/type-definitions/{type}")
    RestResult<FgaTypeDefinitionVO> getTypeDefinition(@PathVariable String storeId, @PathVariable String modelId,
                                                      @PathVariable String type);

    /**
     * 删除类型定义
     *
     * 删除指定类型定义及其下所有关系定义。
     * 只能对草稿状态的模型进行操作。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名，如 document
     * @return 删除结果
     */
    @DeleteMapping("/type-definitions/{type}")
    RestResult<Boolean> deleteTypeDefinition(@PathVariable String storeId, @PathVariable String modelId,
                                             @PathVariable String type);

    // ==================== 关系定义管理 ====================

    /**
     * 添加关系定义
     *
     * 向指定类型添加一个新的关系定义（如 viewer、editor）。
     * 只能对草稿状态的模型进行操作。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名，如 document
     * @param request 关系定义请求
     * @return 创建结果
     */
    @PostMapping("/type-definitions/{type}/relations")
    RestResult<Boolean> addRelation(@PathVariable String storeId,
                                    @PathVariable String modelId,
                                    @PathVariable String type,
                                    @Valid @RequestBody FgaAddRelationRequest request);

    /**
     * 获取关系定义列表
     *
     * 获取指定类型下所有关系定义。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名，如 document
     * @return 关系定义列表
     */
    @GetMapping("/type-definitions/{type}/relations")
    RestResult<List<FgaRelationVO>> listRelations(@PathVariable String storeId,
                                                  @PathVariable String modelId,
                                                  @PathVariable String type);

    /**
     * 获取指定关系定义
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param type         类型名，如 document
     * @param relationName 关系名，如 viewer
     * @return 关系定义详情
     */
    @GetMapping("/type-definitions/{type}/relations/{relationName}")
    RestResult<FgaRelationVO> getRelation(@PathVariable String storeId,
                                          @PathVariable String modelId,
                                          @PathVariable String type,
                                          @PathVariable String relationName);

    /**
     * 更新关系定义
     *
     * 更新指定关系的重写表达式和类型限制。
     * 只能对草稿状态的模型进行操作。
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param type         类型名，如 document
     * @param relationName 关系名，如 viewer
     * @param request      更新请求
     * @return 更新结果
     */
    @PutMapping("/type-definitions/{type}/relations/{relationName}")
    RestResult<Boolean> updateRelation(@PathVariable String storeId,
                                       @PathVariable String modelId,
                                       @PathVariable String type,
                                       @PathVariable String relationName,
                                       @Valid @RequestBody FgaAddRelationRequest request);

    /**
     * 删除关系定义
     *
     * 删除指定关系定义及其类型限制。
     * 只能对草稿状态的模型进行操作。
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param type         类型名，如 document
     * @param relationName 关系名，如 viewer
     * @return 删除结果
     */
    @DeleteMapping("/type-definitions/{type}/relations/{relationName}")
    RestResult<Boolean> deleteRelation(@PathVariable String storeId,
                                       @PathVariable String modelId,
                                       @PathVariable String type,
                                       @PathVariable String relationName);
}
