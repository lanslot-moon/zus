/**
 * 持久化仓储层（基础设施层）
 *
 * <p>本包及其子包负责数据库持久化相关实现，目录结构如下：
 * <pre>
 * repository/
 * ├── I*PersistenceRepository.java   # 持久化接口（技术契约，操作 PO）
 * ├── impl/                          # 持久化实现
 * │   └── *PersistenceRepository.java
 * └── adapter/                       # 领域/查询适配器
 *     ├── *DomainRepositoryAdapter.java
 *     └── *QueryRepositoryAdapter.java
 * </pre>
 *
 * <p>命名规范：
 * <ul>
 *   <li>持久化接口：I*PersistenceRepository（区别于领域层 I*Repository）</li>
 *   <li>持久化实现：*PersistenceRepository（位于 impl 子包）</li>
 *   <li>命令侧适配器：*DomainRepositoryAdapter（位于 adapter 子包）</li>
 *   <li>查询侧适配器：*QueryRepositoryAdapter（位于 adapter 子包）</li>
 * </ul>
 *
 * <p>当前 CQRS 约束：
 * <ul>
 *   <li>AuthorizationModel 的最新已发布模型、分页列表等查询能力由 QueryRepositoryAdapter 承接</li>
 *   <li>AuthorizationModelDomainRepository 仅保留完整聚合装载、保存以及“仅草稿可删”这类命令语义</li>
 *   <li>Tuple / Changelog 的列表、watch、max zookie 等读能力同样不再停留在命令仓储</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure.persistence.mysql.repository;
