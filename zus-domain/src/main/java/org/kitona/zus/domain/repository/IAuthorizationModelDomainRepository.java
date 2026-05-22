package org.kitona.zus.domain.repository;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;

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
 *   <li>TypeDefinition（聚合内实体）</li>
 *   <li>RelationDefinition（值对象）</li>
 * </ul>
 *
 * <p>模型有生命周期状态：草稿(DRAFT) -> 已发布(PUBLISHED) -> 已废弃(ABANDONED)
 *
 * <p>按照 DDD 严格规范，本 Repository 只暴露领域层需要的聚合根访问能力。
 * 列表/分页等读侧查询由独立的查询仓储承担；创建、发布、废弃、删除等用例动作
 * 不应该以技术化方法名泄漏到领域仓储接口。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IAuthorizationModelDomainRepository {

    /**
     * 根据模型ID加载完整聚合根
     *
     * <p>加载内容包括：
     * <ul>
     *   <li>模型基本信息（fga_auth_model）</li>
     *   <li>所有类型定义（fga_type_definition）</li>
     *   <li>所有关系定义（fga_relation_definition）</li>
     *   <li>所有类型限制（fga_type_restriction）</li>
     * </ul>
     *
     * @param id 授权模型聚合根标识
     * @return 完整的授权模型聚合根，不存在返回 empty
     */
    Optional<AuthorizationModelAggregate> findById(AuthorizationModelId id);

    /**
     * 保存授权模型聚合根。
     *
     * <p>Repository 只表达“保存聚合当前状态”，它不是“只新增”的语义。
     * 对基础设施实现来说，底层可以根据聚合是否已经存在选择插入或更新；但这些 insert/update
     * 判断属于持久化细节，不应该通过 {@code create}、{@code update}、{@code saveOrUpdate}
     * 等方法名泄漏到领域仓储接口。
     *
     * <p>授权模型采用 OpenFGA 风格的版本化不可变结构：一个 {@code modelId} 对应一次完整模型写入。
     * 首次保存时需要持久化主表以及 type/relation/restriction/condition 等结构化子表；
     * 已存在的 {@code modelId} 再次保存时，只用于保存聚合生命周期状态、发布时间、DSL 快照等元数据变化，
     * 不应该重写结构化子表。若要修改 type 或 relation，应创建新的 {@code modelId}。
     *
     * <p>创建、发布、废弃、激活等动作属于聚合行为或应用用例，Repository 不通过方法名承载这些业务动作。
     *
     * @param aggregate 授权模型聚合根
     */
    void save(AuthorizationModelAggregate aggregate);

    /**
     * 移除授权模型聚合根。
     *
     * <p>是否允许移除由聚合规则或应用服务在调用前判断，Repository 不通过方法名承载
     * “只能删除草稿”等生命周期业务规则。
     *
     * @param aggregate 授权模型聚合根
     */
    void remove(AuthorizationModelAggregate aggregate);

}
