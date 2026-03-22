/**
 * 领域仓储适配器（DDD Repository Adapter）
 *
 * <p>本包包含命令仓储与查询仓储的基础设施层适配器，职责：
 * <ul>
 *   <li>实现领域层定义的命令仓储接口与查询仓储接口</li>
 *   <li>委托 I*PersistenceRepository 技术契约完成数据库操作</li>
 *   <li>通过 converter 包完成 PO ↔ 聚合 / 读侧视图 的转换</li>
 * </ul>
 *
 * <p>DDD 分层职责：
 * <pre>
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  领域层 (domain)                                                │
 * │  ├── I*DomainRepository：定义命令侧聚合仓储契约                  │
 * │  └── I*QueryRepository：定义查询侧读模型契约                     │
 * ├─────────────────────────────────────────────────────────────────┤
 * │  基础设施层 (infrastructure)                                    │
 * │  ├── adapter/                                                   │
 * │  │   ├── XxxDomainRepositoryAdapter：实现命令仓储，做聚合转换     │
 * │  │   └── XxxQueryRepositoryAdapter：实现查询仓储，做视图转换      │
 * │  ├── impl/                                                      │
 * │  │   └── XxxPersistenceRepository：实现技术契约，操作 PO         │
 * │  └── IXxxPersistenceRepository：定义技术契约，操作 PO            │
 * └─────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <p>适配器边界补充说明：
 * <ul>
 *   <li>AuthorizationModelDomainRepositoryAdapter 不再承接“最新已发布模型”之类的读语义</li>
 *   <li>TupleDomainRepositoryAdapter / ChangelogDomainRepositoryAdapter 仅负责写侧和规则相关查询</li>
 *   <li>TupleQueryRepositoryAdapter / ChangelogQueryRepositoryAdapter 负责列表、过滤、watch 与一致性 token 查询</li>
 *   <li>应用层提交后的异步通知已迁移为应用事件，RepositoryAdapter 不承担事件语义</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure.persistence.mysql.repository.adapter;
