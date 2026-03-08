package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.domain.valueobject.CursorPageResult;

import java.util.List;
import java.util.Optional;

/**
 * 授权模型仓储接口（领域层）
 *
 * <p>授权模型(Authorization Model)是 FGA 系统的核心聚合根，定义了：
 * <ul>
 *   <li>类型定义(Type Definition): 资源类型，如 document、folder、user</li>
 *   <li>关系定义(Relation): 类型之间的关系，如 owner、viewer、parent</li>
 *   <li>关系表达式(Rewrite): 关系的计算规则，支持 union、intersection、difference</li>
 *   <li>类型限制(Restriction): 关系允许的主体类型</li>
 * </ul>
 *
 * <p>聚合边界：
 * <ul>
 *   <li>AuthorizationModelAggregate（聚合根）</li>
 *   <li>TypeDefinitionEntity（聚合内实体）</li>
 *   <li>RelationDefinition（值对象）</li>
 * </ul>
 *
 * <p>模型有生命周期状态：草稿(DRAFT) -> 已发布(PUBLISHED) -> 已废弃(ABANDONED)
 *
 * <p>按照 DDD 严格规范，本 Repository 操作完整的聚合根 {@link AuthorizationModelAggregate}，
 * 包括级联加载/保存所有聚合内实体。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IAuthorizationModelDomainRepository {

    // ==================== 聚合根操作（核心方法） ====================

    /**
     * 根据模型ID加载完整聚合根
     *
     * <p>加载内容包括：
     * <ul>
     *   <li>模型基本信息（fga_authorization_model）</li>
     *   <li>所有类型定义（fga_type_definition）</li>
     *   <li>所有关系定义（fga_model_relation）</li>
     *   <li>所有类型限制（fga_relation_restriction）</li>
     * </ul>
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型唯一标识
     * @return 完整的授权模型聚合根，不存在返回 empty
     */
    Optional<AuthorizationModelAggregate> findByModelId(String storeId, String modelId);

    /**
     * 保存完整聚合根（新增或更新）
     *
     * <p>级联保存内容：
     * <ul>
     *   <li>模型基本信息</li>
     *   <li>所有类型定义（先删除再插入）</li>
     *   <li>所有关系定义（先删除再插入）</li>
     *   <li>所有类型限制（先删除再插入）</li>
     * </ul>
     *
     * @param aggregate 授权模型聚合根
     * @return 保存成功返回 true
     */
    boolean saveOrUpdateModel(AuthorizationModelAggregate aggregate);

    /**
     * 删除完整聚合根（级联删除，仅限草稿状态）
     *
     * <p>级联删除内容：
     * <ul>
     *   <li>模型基本信息</li>
     *   <li>所有类型定义</li>
     *   <li>所有关系定义</li>
     *   <li>所有类型限制</li>
     * </ul>
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型ID
     * @return 删除成功返回 true
     */
    boolean deleteModel(String storeId, String modelId);

    // ==================== 查询方法 ====================

    /**
     * 查询存储空间下的所有授权模型（不加载类型定义）
     *
     * <p>轻量级查询，只加载模型基本信息，适用于列表展示场景。
     * 如需完整聚合，请使用 {@link #findByModelId(String, String)}。
     *
     * @param storeId 存储空间ID
     * @return 授权模型聚合根列表（不含类型定义）
     */
    List<AuthorizationModelAggregate> findByStoreId(String storeId);

    /**
     * 根据状态查询授权模型列表（不加载类型定义）
     *
     * @param storeId 存储空间ID
     * @param status  状态
     * @return 授权模型聚合根列表（不含类型定义）
     */
    List<AuthorizationModelAggregate> findByStoreIdAndStatus(String storeId, ModelPublishStatus status);

    /**
     * 查询存储空间下最新发布的授权模型（加载完整聚合）
     *
     * @param storeId 存储空间ID
     * @return 最新发布的完整授权模型聚合根，不存在返回 empty
     */
    Optional<AuthorizationModelAggregate> findLatestByStoreId(String storeId);

    /**
     * 分页查询授权模型（游标分页，不加载类型定义）
     *
     * <p>游标分页相比偏移分页的优势：
     * <ul>
     *   <li>性能更好：避免 OFFSET 导致的深分页性能问题</li>
     *   <li>数据一致性：不会因为数据插入/删除导致重复或遗漏</li>
     * </ul>
     *
     * @param storeId   存储空间ID
     * @param status    状态过滤，可为 null 表示不过滤
     * @param pageToken 分页游标（上一页最后一条的模型ID），首页传 null
     * @param pageSize  每页数量
     * @return 游标分页结果，包含数据列表和下一页游标
     */
    CursorPageResult<AuthorizationModelAggregate> findPageByCursor(String storeId, Integer status, String pageToken, int pageSize);

    // ==================== 状态变更方法 ====================

    /**
     * 发布授权模型（草稿 -> 已发布）
     *
     * <p>发布后模型不可修改，成为只读状态。
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型ID
     * @return 发布成功返回 true
     */
    boolean publishModel(String storeId, String modelId);

    /**
     * 废弃授权模型（已发布 -> 已废弃）
     *
     * <p>废弃后模型仍可用于历史查询，但不会被用于新的权限检查。
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型ID
     * @return 废弃成功返回 true
     */
    boolean deprecateModel(String storeId, String modelId);

    // ==================== 统计方法 ====================

    /**
     * 统计存储空间下已发布模型数量
     *
     * @param storeId 存储空间ID
     * @return 已发布模型数量
     */
    long countPublishedModels(String storeId);

    /**
     * 检查模型是否存在
     *
     * @param storeId 存储空间ID
     * @param modelId 授权模型ID
     * @return 存在返回 true
     */
    boolean existsByModelId(String storeId, String modelId);
}
