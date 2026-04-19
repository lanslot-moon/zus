package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;

/**
 * 授权模型应用服务接口
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IAuthorizationModelApplicationService {

    /**
     * 创建授权模型
     *
     * <p>创建后模型状态为草稿(status=0)。返回创建后的模型 DTO，
     * 调用方可据此直接拿到自动生成的 {@code modelId}，无需额外查询。
     *
     * @param command 创建命令
     * @return 新创建的模型 DTO
     */
    AuthorizationModelResultDTO createModel(CreateModelCommand command);

    /**
     * 获取授权模型
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 模型详情
     */
    AuthorizationModelResultDTO getModel(String storeId, String modelId);

    /**
     * 获取 Store 当前激活的授权模型
     *
     * <p>若 Store 未绑定任何模型，返回 {@code null}。
     *
     * @param storeId 存储空间ID
     * @return 当前激活模型 DTO；未绑定返回 null
     */
    AuthorizationModelResultDTO getCurrentModel(String storeId);

    /**
     * 列出存储空间下的授权模型（支持分页和状态筛选）
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResultDTO<AuthorizationModelResultDTO> listModels(ListModelsQuery query);

    /**
     * 发布授权模型（草稿 -> 已发布）
     *
     * <p>发布后模型状态变为「已发布」，但不会自动激活。
     * <p>需调用 {@link #activateModel(String, String)} 将其设为当前生效模型。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 发布成功返回 true
     */
    boolean publishModel(String storeId, String modelId);

    /**
     * 激活授权模型（将已发布的模型设为当前生效）
     *
     * <p>前置条件：模型必须是已发布状态（PUBLISHED）
     * <p>激活后，Store 的 currentModelId 在命令事务内同步指向该模型，
     * 接口成功即表示后续 Check 请求可立即使用此模型。
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 激活成功返回 true
     */
    boolean activateModel(String storeId, String modelId);

    /**
     * 废弃授权模型（已发布 -> 已废弃）
     *
     * <p>废弃后模型仍可用于历史查询，但不会被用于新的权限检查
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 废弃成功返回 true
     */
    boolean deprecateModel(String storeId, String modelId);

    /**
     * 删除授权模型
     *
     * 只能删除草稿状态(status=0)的模型
     *
     * @param storeId 存储空间ID
     * @param modelId 模型ID
     * @return 是否删除成功
     */
    boolean deleteModel(String storeId, String modelId);
}
