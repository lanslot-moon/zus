/**
 * ZUS 权限系统基础设施层（Infrastructure Layer）
 *
 * <p>本包提供技术实现与外部依赖的适配，遵循 DDD 中“依赖倒置”原则：
 * 领域层定义仓储与防腐层接口，本层提供具体实现。
 *
 * <h2>包结构说明</h2>
 *
 * <pre>
 * infrastructure/
 * ├── cache/          缓存（Zookie、Tuple 存在性等）
 * │   ├── config/     Caffeine、Redis 配置
 * │   └── query/      缓存查询封装
 * ├── context/        运行时上下文（如当前用户）
 * ├── engine/         领域引擎实现（解析器、图工厂、元组存储）
 * │   ├── factory/    IModelGraphFactory、ITupleStoreFactory 实现
 * │   ├── parser/     OpenFGA 模型解析（ANTLR4）
 * │   └── tuple/      RepositoryTupleStore、InMemoryTupleStore
 * ├── enums/          基础设施枚举（与表/缓存状态对应）
 * ├── external/       外部系统适配（防腐层实现）
 * │   └── usercenter/ 用户中心 IUserInfoGateway 实现
 * ├── handler/        MyBatis-Plus 自动填充等
 * └── persistence/    持久化
 *     └── mysql/      MyBatis-Plus + MySQL
 *         ├── converter/  PO 与领域对象转换
 *         ├── entity/     持久化对象（PO）
 *         ├── mapper/     MyBatis Mapper 接口
 *         └── repository/ 仓储接口（PO 级）与实现、领域适配器
 * </pre>
 *
 * <h2>依赖规则</h2>
 * <ul>
 *   <li>依赖领域层：实现领域定义的 Repository、Port 接口</li>
 *   <li>也可实现领域模块中单独声明的共享协作契约（如 {@code domain.gateway} 下的外部查询网关）</li>
 *   <li>基础设施层可以被应用层装配和调用，但不反向依赖 API 协议对象</li>
 *   <li>不暴露领域模型：对外仅通过领域接口与 DTO/PO 交互</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure;
