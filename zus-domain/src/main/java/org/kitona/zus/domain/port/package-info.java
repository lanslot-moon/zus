/**
 * 端口包（Ports）
 *
 * <p>六边形架构中的端口定义，包含领域层与外部世界交互的契约接口。
 *
 * <h2>端口说明</h2>
 * <p>本包只包含被驱动端口（Driven Ports），即领域层需要的外部能力。
 * 读侧查询仓储不定义在本包，而是作为领域查询契约放在 {@code domain.repository}
 * 或 {@code domain.query} 相关模型旁边。
 * 仅用于 DTO 组装或读侧补充的外部查询协作，放在 {@code domain.gateway} 包中。
 *
 * <h2>包含的端口</h2>
 * <ul>
 *   <li>{@link IModelCompiler} - 模型编译器，编译 DSL 表达式</li>
 *   <li>{@link ITupleStore} - 元组存储，供权限检查器查询</li>
 *   <li>{@link ITupleStoreFactory} - 元组存储工厂，按 store 与 zookie 构造一致性视图</li>
 *   <li>{@link IZookieSequencePort} - zookie 序列能力，供 tuple 写入领域服务生成一致性版本</li>
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
