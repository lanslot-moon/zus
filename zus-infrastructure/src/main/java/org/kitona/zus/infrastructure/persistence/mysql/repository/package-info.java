/**
 * 持久化仓储层（基础设施层）
 *
 * <p>本包及其子包负责数据库持久化相关实现，目录结构如下：
 * <pre>
 * repository/
 * ├── I*PersistenceRepository.java   # 持久化接口（技术契约，操作 PO）
 * ├── impl/                          # 持久化实现
 * │   └── *PersistenceRepository.java
 * └── adapter/                       # 领域适配器
 *     └── *RepositoryDomainAdapter.java
 * </pre>
 *
 * <p>命名规范：
 * <ul>
 *   <li>持久化接口：I*PersistenceRepository（区别于领域层 I*Repository）</li>
 *   <li>持久化实现：*PersistenceRepository（位于 impl 子包）</li>
 *   <li>领域适配器：*RepositoryDomainAdapter（位于 adapter 子包）</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
package org.kitona.zus.infrastructure.persistence.mysql.repository;
