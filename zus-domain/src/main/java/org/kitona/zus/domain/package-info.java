/**
 * ZUS 权限系统领域层（Domain Layer）
 *
 * <p>本包是 FGA 权限系统的核心领域层，遵循 DDD（领域驱动设计）和六边形架构规范。
 *
 * <h2>包结构说明（六边形架构）</h2>
 *
 * <pre>
 * domain/
 * │
 * ├── authorization/     授权领域上下文
 * │   ├── store/                     Store 聚合
 * │   ├── model/                     Authorization Model 聚合
 * │   ├── tuple/                     Tuple 子域
 * │   ├── audit/                     审计子域
 * │   └── evaluation/                编译模型与求值子域
 * │
 * ├── valueobject/       值对象（Value Object）
 * │   ├── Zookie                      一致性令牌
 * │   ├── PermissionCheckResult       鉴权结果
 * │   ├── PermissionCheckStatus       鉴权结果状态
 * │   └── ...
 * │
 * ├── enums/             领域枚举
 * │   ├── ModelPublishStatus          模型发布状态
 * │   └── StoreStatus                 存储空间状态
 * │
 * ├── repository/        仓储接口（Repository Interface）
 * │   ├── IStoreDomainRepository      存储空间仓储
 * │   ├── IAuthorizationModelDomainRepository 授权模型仓储
 * │   ├── ITupleDomainRepository      元组命令仓储
 * │   ├── IChangelogDomainRepository  变更日志命令仓储
 * │   ├── IStoreQueryRepository       存储空间查询仓储
 * │   ├── IAuthorizationModelQueryRepository 授权模型查询仓储
 * │   ├── ITupleQueryRepository       元组查询仓储
 * │   └── IChangelogQueryRepository   变更日志查询仓储
 * │
 * ├── read/              读侧契约
 * │   ├── criteria/                 查询条件对象
 * │   └── view/                     读侧结果视图
 * │
 * ├── service/           领域服务（Domain Service）
 * │   ├── PermissionEvaluator        统一权限求值器
 * │   └── TupleMutationDomainService tuple 变更领域服务
 * │
 * └── port/              端口（Ports，六边形架构）
 *     ├── ICompiledModelCompiler     编译模型编译端口
 *     ├── ICompiledModelCache        编译模型缓存端口
 *     ├── IConditionEvaluator        条件求值端口
 *     ├── IModelSnapshotRenderer     模型快照渲染端口
 *     ├── IAuditContextProvider      审计上下文端口
 *     ├── IZookieSequencePort        zookie 序列端口
 *     └── ...                        领域规则真正依赖的外部能力
 * </pre>
 *
 * <h2>六边形架构说明</h2>
 *
 * <h3>端口分类</h3>
 * <ul>
 *   <li><b>驱动端口（Driving/Inbound Ports）</b>：由应用层直接调用领域服务，无需单独定义接口</li>
 *   <li><b>被驱动端口（Driven/Outbound Ports）</b>：领域层依赖的外部能力，定义在 {@code port/outbound/}</li>
 * </ul>
 *
 * <h3>端口与适配器</h3>
 * <ul>
 *   <li>端口（Port）：定义在领域层，是契约接口</li>
 *   <li>适配器（Adapter）：实现在基础设施层，是具体实现</li>
 * </ul>
 *
 * <h2>聚合设计</h2>
 *
 * <h3>1. StoreAggregate（存储空间聚合）</h3>
 * <p>权限数据的逻辑隔离单元，是系统顶层聚合根。
 * <ul>
 *   <li>业务创建：{@code StoreAggregate.create(storeId, name, description)}</li>
 *   <li>持久化重建：{@code StoreAggregate.reconstitute(StoreSnapshot)}</li>
 * </ul>
 *
 * <h3>2. AuthorizationModelAggregate（授权模型聚合）</h3>
 * <p>定义权限关系的模型，包含类型定义、关系定义、类型限制与条件定义。
 * <ul>
 *   <li>聚合根：AuthorizationModelAggregate</li>
 *   <li>聚合内实体：TypeDefinition</li>
 *   <li>值对象：RelationDefinition</li>
 *   <li>命令仓储只负责完整聚合装载、保存与“仅草稿可删”这类不变量封装</li>
 *   <li>最新已发布模型、分页列表等读语义由查询仓储承担</li>
 * </ul>
 *
 * <h3>3. RelationTuple（关系元组）</h3>
 * <p>具体的权限关系数据实例，显式承载 wildcard、过期时间、条件与 zookie 语义。
 *
 * <h3>4. evaluation（授权求值子域）</h3>
 * <p>用于承载授权模型编译产物、运行时上下文、求值规格和 rewrite AST。
 * <ul>
 *   <li>{@code compiled}: 已编译模型与关系</li>
 *   <li>{@code runtime}: 单次求值请求、递归保护、运行时模板</li>
 *   <li>{@code specification}: tuple 可见性、主体匹配、关系限制规则</li>
 *   <li>{@code nodes}: rewrite 抽象语法树</li>
 *   <li>{@code strategy}: 节点求值策略</li>
 * </ul>
 *
 * <h2>依赖规则</h2>
 * <ul>
 *   <li>领域层是系统核心，不依赖任何外部框架（无 Spring 注解）</li>
 *   <li>命令仓储与查询仓储接口定义在领域层，实现在基础设施层</li>
 *   <li>领域层不再承载由应用层直接发布的“伪领域事件”，提交后通知改由应用事件表达</li>
 *   <li>Port 接口定义在领域层，Adapter 实现在基础设施层</li>
 * </ul>
 *
 * @author kitona
 * @version 2.0.0
 * @since 2025-01-15
 */
package org.kitona.zus.domain;
