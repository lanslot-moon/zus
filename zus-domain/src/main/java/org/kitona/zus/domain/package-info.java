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
 * ├── aggregate/         聚合根（Aggregate Root）与重建快照
 * │   ├── StoreAggregate              存储空间聚合根
 * │   ├── StoreSnapshot               存储空间持久化快照
 * │   ├── AuthorizationModelAggregate 授权模型聚合根
 * │   └── AuthorizationModelSnapshot  授权模型持久化快照
 * │
 * ├── entity/            聚合内实体（Entity）
 * │   ├── TypeDefinitionEntity        类型定义实体
 * │   ├── RelationTupleEntity         关系元组实体
 * │   └── ChangelogEntity             变更日志实体
 * │
 * ├── valueobject/       值对象（Value Object）
 * │   ├── Zookie                      一致性令牌
 * │   ├── TupleKey                    元组键
 * │   ├── RelationDefinition          关系定义
 * │   ├── TypeDefinition              类型定义（运行时用）
 * │   └── ...
 * │
 * ├── enums/             领域枚举
 * │   ├── ModelPublishStatus          模型发布状态
 * │   └── StoreStatus                 存储空间状态
 * │
 * ├── repository/        仓储接口（Repository Interface，仅聚合根）
 * │   ├── IStoreDomainRepository      存储空间仓储
 * │   ├── IAuthorizationModelDomainRepository 授权模型仓储
 * │   └── ITupleDomainRepository      元组仓储
 * │
 * ├── service/           领域服务（Domain Service）
 * │   ├── AuthorizationChecker        权限检查器
 * │   ├── AuthorizationModelGraph     授权模型图
 * │   └── internal/      内部实现（不对外暴露）
 * │       ├── DirectedGraph           有向图数据结构
 * │       ├── GraphNode               图节点
 * │       ├── NodeType                节点类型枚举
 * │       ├── GraphBuilder            图构建器
 * │       └── ModelTextParser         模型文本解析器
 * │
 * ├── port/              端口（Ports，六边形架构）
 * │   ├── IModelCompiler              模型编译器端口
 * │   ├── ITupleStore                 元组存储端口
 * │   ├── IUserInfoAdapter            用户信息适配器（防腐层）
 * │   └── IChangelogQueryPort         变更日志查询端口
 * │
 * ├── event/             领域事件（Domain Event）
 * │   ├── DomainEvent                 事件基类
 * │   ├── TupleWrittenEvent           元组写入事件
 * │   ├── TupleDeletedEvent           元组删除事件
 * │   └── ModelUpdatedEvent           模型更新事件
 * │
 * └── factory/           工厂（Factory）
 *     └── AuthorizationModelFactory   授权模型工厂
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
 * <p>定义权限关系的模型，包含类型定义和关系定义。
 * <ul>
 *   <li>聚合根：AuthorizationModelAggregate</li>
 *   <li>聚合内实体：TypeDefinitionEntity</li>
 *   <li>值对象：RelationDefinition</li>
 * </ul>
 *
 * <h3>3. RelationTupleEntity（关系元组）</h3>
 * <p>具体的权限关系数据实例。
 *
 * <h2>依赖规则</h2>
 * <ul>
 *   <li>领域层是系统核心，不依赖任何外部框架（无 Spring 注解）</li>
 *   <li>Repository 接口定义在领域层，实现在基础设施层</li>
 *   <li>Port 接口定义在领域层，Adapter 实现在基础设施层</li>
 * </ul>
 *
 * @author kitona
 * @version 2.0.0
 * @since 2025-01-15
 */
package org.kitona.zus.domain;
