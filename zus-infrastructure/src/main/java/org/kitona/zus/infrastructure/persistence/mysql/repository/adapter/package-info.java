/**
 * 领域仓储适配器（DDD Repository Adapter）
 *
 * <p>本包包含所有领域仓储接口的基础设施层实现（适配器），职责：
 * <ul>
 *   <li>实现领域层定义的 IXxxRepository 接口</li>
 *   <li>委托 impl 包中的 XxxPersistenceRepository 完成数据库操作</li>
 *   <li>通过 converter 包完成 PO ↔ 领域对象（Entity/Aggregate/BO）的转换</li>
 * </ul>
 *
 * <p>DDD 分层职责：
 * <pre>
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  领域层 (domain)                                                │
 * │  └── IXxxRepository：定义业务契约，操作领域对象                   │
 * ├─────────────────────────────────────────────────────────────────┤
 * │  基础设施层 (infrastructure)                                    │
 * │  ├── adapter/                                                   │
 * │  │   └── XxxRepositoryDomainAdapter：实现领域接口，做模型转换     │
 * │  ├── impl/                                                      │
 * │  │   └── XxxPersistenceRepository：实现持久化接口，操作 PO       │
 * │  └── IXxxPersistenceRepository：定义技术契约，操作 PO            │
 * └─────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;
