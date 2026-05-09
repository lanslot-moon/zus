package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.AddRelationCommand;
import org.kitona.zus.service.dto.command.AddTypeDefinitionCommand;
import org.kitona.zus.service.dto.response.AuthorizationRelationResultDTO;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;

import java.util.List;

/**
 * 授权模型结构（Schema）应用服务接口
 *
 * <p>按<strong>领域能力</strong>划分：负责「管理授权模型的结构定义」这一完整能力，
 * 包括类型定义（TypeDefinition）与关系定义（RelationDefinition）的增删改查。
 *
 * <p>与 {@link IAuthorizationModelApplicationService} 的职责边界：
 * <ul>
 *   <li>{@link IAuthorizationModelApplicationService}：模型的<strong>生命周期</strong>（创建、发布、废弃、删除等）</li>
 *   <li>本接口：模型的<strong>结构内容</strong>（类型与关系的编辑，仅作用于草稿态模型）</li>
 * </ul>
 *
 * <p>所有操作通过聚合根 AuthorizationModelAggregate 进行，保证聚合一致性。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 * @see IAuthorizationModelApplicationService
 */
public interface IAuthorizationModelSchemaApplicationService {

    /**
     * 添加类型定义
     *
     * <p>仅支持对草稿状态的模型操作。类型名不可与已有类型重复。
     *
     * @param command 添加类型定义命令，含 storeId、modelId、type、relations、relationRestrictions
     * @return 添加成功返回 true
     */
    boolean addTypeDefinition(AddTypeDefinitionCommand command);

    /**
     * 获取类型定义列表
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 类型定义列表，不存在或为空时返回空列表
     */
    List<AuthorizationTypeDefinitionResultDTO> listTypeDefinitions(String storeId, String modelId);

    /**
     * 获取指定类型定义
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名
     * @return 类型定义，不存在时抛出异常
     */
    AuthorizationTypeDefinitionResultDTO getTypeDefinition(String storeId, String modelId, String type);

    /**
     * 删除类型定义
     *
     * <p>仅支持草稿态模型。删除类型会同时删除该类型下所有关系定义。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名
     * @return 删除成功返回 true，类型不存在返回 false
     */
    boolean deleteTypeDefinition(String storeId, String modelId, String type);



    // ==================== 关系定义 ====================

    /**
     * 添加关系定义
     *
     * <p>仅支持草稿态模型。关系名在同一类型下不可重复。
     *
     * @param command 添加关系定义命令，含 storeId、modelId、type、relationName、rewriteExpression、allowedTypes
     * @return 添加成功返回 true
     */
    boolean addRelation(AddRelationCommand command);

    /**
     * 获取关系定义列表
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @param type    类型名
     * @return 关系定义列表，不存在或为空时返回空列表
     */
    List<AuthorizationRelationResultDTO> listRelations(String storeId, String modelId, String type);

    /**
     * 获取指定关系定义
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param type         类型名
     * @param relationName 关系名
     * @return 关系定义，不存在时抛出异常
     */
    AuthorizationRelationResultDTO getRelation(String storeId, String modelId, String type, String relationName);

    /**
     * 更新关系定义
     *
     * <p>仅支持草稿态模型。可更新重写表达式与类型限制。
     *
     * @param command 更新关系定义命令
     * @return 更新成功返回 true
     */
    boolean updateRelation(AddRelationCommand command);

    /**
     * 删除关系定义
     *
     * <p>仅支持草稿态模型。
     *
     * @param storeId      存储空间ID
     * @param modelId      模型ID
     * @param type         类型名
     * @param relationName 关系名
     * @return 删除成功返回 true，关系或类型不存在返回 false
     */
    boolean deleteRelation(String storeId, String modelId, String type, String relationName);
}
