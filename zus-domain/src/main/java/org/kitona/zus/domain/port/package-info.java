/**
 * 端口包（Ports）
 *
 * <p>六边形架构中的端口定义，包含领域层与外部世界交互的契约接口。
 *
 * <h2>端口说明</h2>
 * <p>本包只包含被驱动端口（Driven Ports），即领域层需要的外部能力。
 * 驱动端口（Driving Ports）由应用层直接调用领域服务，无需单独定义接口。
 *
 * <h2>包含的端口</h2>
 * <ul>
 *   <li>{@link IModelCompiler} - 模型编译器，编译 DSL 表达式</li>
 *   <li>{@link ITupleStore} - 元组存储，供权限检查器查询</li>
 *   <li>{@link IUserInfoAdapter} - 用户信息适配器（防腐层）</li>
 *   <li>{@link IChangelogQueryPort} - 变更日志查询</li>
 * </ul>
 *
 * <h2>实现位置</h2>
 * <p>所有端口的适配器实现都位于 {@code zus-infrastructure} 模块。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
package org.kitona.zus.domain.port;
