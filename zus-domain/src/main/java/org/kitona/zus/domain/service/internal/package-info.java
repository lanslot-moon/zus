/**
 * 领域服务内部实现包
 *
 * <p>本包包含领域服务的内部实现类，这些类是引擎的内部细节，
 * 不应被领域层外部直接使用。
 *
 * <h2>包内类说明</h2>
 * <ul>
 *   <li>{@link org.kitona.zus.domain.service.internal.DirectedGraph} - 有向图数据结构</li>
 *   <li>{@link org.kitona.zus.domain.service.internal.GraphNode} - 图节点</li>
 *   <li>{@link org.kitona.zus.domain.service.internal.NodeType} - 节点类型枚举</li>
 *   <li>{@link org.kitona.zus.domain.service.internal.GraphBuilder} - 图构建器</li>
 *   <li>{@link org.kitona.zus.domain.service.internal.ModelTextParser} - 模型文本解析器</li>
 * </ul>
 *
 * <h2>对外暴露</h2>
 * <p>外部应通过 {@link org.kitona.zus.domain.service.AuthorizationModelGraph} 使用图功能，
 * 不应直接访问本包内的类。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
package org.kitona.zus.domain.service.internal;
