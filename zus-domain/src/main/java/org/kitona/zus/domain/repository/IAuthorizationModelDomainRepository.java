package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;

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
 * <p>按照 DDD 严格规范，本 Repository 只操作完整聚合根
 * {@link AuthorizationModelAggregate}，包括级联加载/保存所有聚合内实体。
 * 列表/分页等读侧查询由独立的查询仓储承担。
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
     * 删除草稿状态的完整聚合根（级联删除）
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
    boolean deleteDraftModel(String storeId, String modelId);

}
