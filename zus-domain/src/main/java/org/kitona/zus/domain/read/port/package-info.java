/**
 * 读侧查询端口。
 *
 * <p>这些端口由应用层读用例调用、由基础设施层适配实现，返回 read view 而非聚合根。
 * 它们与 {@code domain.repository} 的聚合仓储分离，避免 Repository 同时承担命令模型和查询模型。
 */
package org.kitona.zus.domain.read.port;
