/**
 * 端口包（Ports）
 *
 * <p>六边形架构中的端口定义，包含领域层与外部世界交互的契约接口。
 *
 * <h2>端口说明</h2>
 * <p>本包只包含被驱动端口（Driven Ports），即领域层需要的外部能力。
 * 读侧查询仓储不定义在本包，而是作为领域查询契约放在 {@code domain.read.port}
 * 相关模型旁边。
 *
 * <h2>包含的端口</h2>
 * <ul>
 *   <li>{@link ICompiledModelCompiler} - 结构化模型编译为已编译授权模型</li>
 *   <li>{@link IConditionEvaluator} - 条件表达式求值</li>
 *   <li>{@link IModelSnapshotRenderer} - 授权模型 DSL 快照渲染</li>
 *   <li>{@link IAuditContextProvider} - 审计上下文提供能力</li>
 *   <li>{@link IDirectTupleReader} - direct tuple 读取，SELF 证据与 TTU 传播边都来自同一事实读取能力</li>
 *   <li>{@link ISubjectObjectCandidateReader} - object 候选读取</li>
 *   <li>{@link IObjectSubjectCandidateReader} - subject 候选读取</li>
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
